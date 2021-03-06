package com.muhil.zohokart.fragments;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Order;
import com.muhil.zohokart.models.OrderLineItem;
import com.muhil.zohokart.models.PaymentCard;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.services.OrderDeliveryService;
import com.muhil.zohokart.utils.CardValidator;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentFragment extends android.support.v4.app.Fragment
{

    public static final Integer[] MONTHS = {1, 2, 3, 4, 5, 6, 7, 8, 9,10, 11, 12};
    public static Integer[] YEARS = new Integer[10];
    public static boolean SAVED_CARD_SELECTED = false;
    public static boolean COD_SELECTED = false;

    AlarmManager alarmManager;
    PendingIntent orderDeliveryService;
    Calendar calendar, deliveryServiceCalendar;
    ZohokartDAO zohokartDAO;
    View rootView;
    EditText cardNumberText, nameOnCardText;
    TextView cardType;
    RadioGroup paymentOptions;
    LinearLayout savedCardHolder;
    String email;
    List<PaymentCard> savedCards;
    PaymentCard selectedCard;
    List<CardView> cardViews;
    LayoutInflater inflater;
    CardView card_item_view;
    Spinner monthSpinner, yearSpinner;
    int year;
    PaymentCommunicator communicator;
    SharedPreferences orderPref;
    SharedPreferences.Editor orderEditor;

    public static PaymentFragment getInstance(String email, List<Integer> productIds, List<Integer> quantities)
    {
        PaymentFragment paymentFragment = new PaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Account.EMAIL, email);
        bundle.putIntegerArrayList("product_ids", (ArrayList<Integer>) productIds);
        bundle.putIntegerArrayList("quantities", (ArrayList<Integer>) quantities);
        paymentFragment.setArguments(bundle);
        return paymentFragment;
    }

    public PaymentFragment()
    {
        // Required empty public constructor
    }

    public void setCommunicator(PaymentCommunicator communicator)
    {
        this.communicator = communicator;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        zohokartDAO = new ZohokartDAO(getActivity());
        email = getArguments().getString(Account.EMAIL);
        this.inflater = LayoutInflater.from(getActivity());
        calendar = Calendar.getInstance();
        deliveryServiceCalendar = Calendar.getInstance();
        cardViews = new ArrayList<>();
        orderPref = getActivity().getSharedPreferences(ZohoKartSharePreferences.ORDER, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_payment, container, false);
        this.cardNumberText = (EditText) rootView.findViewById(R.id.card_number);
        this.cardType = (TextView) rootView.findViewById(R.id.new_card_type);
        this.nameOnCardText = (EditText) rootView.findViewById(R.id.name_on_card);
        savedCardHolder = (LinearLayout) rootView.findViewById(R.id.saved_card_holder);
        paymentOptions = (RadioGroup) rootView.findViewById(R.id.payment_options);
        monthSpinner = (Spinner) rootView.findViewById(R.id.expiry_month);
        yearSpinner = (Spinner) rootView.findViewById(R.id.expiry_year);
        monthSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, MONTHS));
        year = calendar.get(Calendar.YEAR);
        for (int i = 0; i<10; i++)
        {
            YEARS[i] = year+i;
        }
        yearSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, YEARS));

        ((EditText) rootView.findViewById(R.id.card_number)).addTextChangedListener(
                new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {

                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
                        Log.d("TEXT_CHANGED", "" + s.length());
                        if (s.length() == 2)
                        {
                            if (Integer.parseInt(String.valueOf(s.subSequence(0, 2))) >= 40 && Integer.parseInt(String.valueOf(s.subSequence(0, 2))) <= 49)
                            {
                                cardType.setText("Visa");
                            }
                            else if (Integer.parseInt(String.valueOf(s.subSequence(0, 2))) >= 51 && Integer.parseInt(String.valueOf(s.subSequence(0, 2))) <= 55)
                            {
                                cardType.setText("Mastercard");
                            }
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s)
                    {

                    }
                }
        );

        paymentOptions.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId)
                    {
                        if (checkedId == R.id.cod_action)
                        {
                            COD_SELECTED = true;
                            (rootView.findViewById(R.id.card_detail)).setVisibility(View.GONE);
                        } else if (checkedId == R.id.credit_card_action)
                        {
                            (rootView.findViewById(R.id.card_detail)).setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        rootView.findViewById(R.id.make_payment_action).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        if (COD_SELECTED)
                        {
                            showConfirmOrder();
                        }
                        else if (SAVED_CARD_SELECTED && selectedCard != null)
                        {
                            showConfirmOrder();
                        }
                        else
                        {
                            final String cardNumber = PaymentFragment.this.cardNumberText.getText().toString();
                            final String nameOnCard = PaymentFragment.this.nameOnCardText.getText().toString();
                            final String expiryMonth =String.valueOf(monthSpinner.getSelectedItem());
                            final String expiryYear =String.valueOf(yearSpinner.getSelectedItem());
                            if (cardNumber.length() > 0 && cardNumber.length() == 16)
                            {
                                if (!(CardValidator.ifAnyLetterExist(cardNumber)))
                                {
                                    if ((Integer.parseInt(cardNumber.substring(0,2)) >=40 && Integer.parseInt(cardNumber.substring(0,2)) <= 49) ||
                                            (Integer.parseInt(cardNumber.substring(0,2)) >=50 && Integer.parseInt(cardNumber.substring(0,2)) <= 59))
                                    {
                                        if (!(CardValidator.ifAnyDigitExist(nameOnCard)))
                                        {
                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                            alertDialogBuilder.setMessage("Do you want to save the card for future purchases?");
                                            alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    if ((Integer.parseInt(cardNumber.substring(0,2)) >= 40 && Integer.parseInt(cardNumber.substring(0,2)) <= 49))
                                                    {
                                                        new AddingCardTask().execute(cardNumber, "visa", nameOnCard, expiryMonth + "/" + expiryYear, email);
                                                    }
                                                    else if ((Integer.parseInt(cardNumber.substring(0,2)) >= 50 && Integer.parseInt(cardNumber.substring(0,2)) <= 59))
                                                    {
                                                        new AddingCardTask().execute(cardNumber, "Mastercard", nameOnCard, expiryMonth + "/" + expiryYear, email);
                                                    }
                                                    dialog.dismiss();
                                                    showConfirmOrder();
                                                }
                                            });
                                            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    dialog.dismiss();
                                                }
                                            });
                                            alertDialogBuilder.show();
                                        }
                                        else
                                        {
                                            Toast.makeText(getActivity(), "Enter a valid name.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(getActivity(), "Enter a valid card number.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(getActivity(), "Enter a valid card number.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(getActivity(), "Enter a valid card number.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }
        );

        new SavedCardListingTask().execute(email);

        return rootView;
    }

    class SavedCardListingTask extends AsyncTask<String, Void, List<PaymentCard>>
    {

        @Override
        protected List<PaymentCard> doInBackground(String... emails)
        {
            savedCards = zohokartDAO.getCards(emails[0]);
            return savedCards;
        }

        @Override
        protected void onPostExecute(List<PaymentCard> paymentCards)
        {
            super.onPostExecute(paymentCards);

            if (paymentCards != null && paymentCards.size() > 0)
            {
                for (PaymentCard paymentCard : paymentCards)
                {
                    card_item_view  = (CardView) inflater.inflate(R.layout.saved_card_item_view, savedCardHolder, false);
                    ((TextView) card_item_view.findViewById(R.id.card_number)).setText("XXXX-XXXX-XXXX-" + paymentCard.getCardNumber().substring((paymentCard.getCardNumber().length() - 4), paymentCard.getCardNumber().length()));
                    ((TextView) card_item_view.findViewById(R.id.card_type)).setText(paymentCard.getCardType());
                    card_item_view.setTag(paymentCard);
                    card_item_view.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    if (v.findViewById(R.id.card_selected).getVisibility() == View.VISIBLE)
                                    {
                                        clearSelection();
                                        selectedCard = null;
                                        SAVED_CARD_SELECTED = false;
                                        rootView.findViewById(R.id.new_card_detail).setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        clearSelection();
                                        v.findViewById(R.id.card_selected).setVisibility(View.VISIBLE);
                                        selectedCard = (PaymentCard) v.getTag();
                                        SAVED_CARD_SELECTED = true;
                                        rootView.findViewById(R.id.new_card_detail).setVisibility(View.GONE);
                                    }
                                }
                            }
                    );

                    savedCardHolder.addView(card_item_view);
                    cardViews.add(card_item_view);
                }
                rootView.findViewById(R.id.cards_empty).setVisibility(View.GONE);
            }
            else
            {

                rootView.findViewById(R.id.cards_empty).setVisibility(View.VISIBLE);
            }

        }
    }

    class AddingCardTask extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... cardDetails)
        {
            PaymentCard paymentCard = new PaymentCard();
            paymentCard.setCardNumber(cardDetails[0]);
            paymentCard.setCardType(cardDetails[1]);
            paymentCard.setNameOnCard(cardDetails[2]);
            paymentCard.setExpiryDate(cardDetails[3]);
            zohokartDAO.addCard(paymentCard, cardDetails[4]);
            return null;
        }
    }

    private void clearSelection()
    {
        for (CardView cardView : cardViews)
        {
            cardView.findViewById(R.id.card_selected).setVisibility(View.GONE);
        }
    }

    public void showConfirmOrder()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("");
        alertDialogBuilder.setMessage("Confirm order?");

        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                new PlaceOrderTask().execute(PaymentFragment.this.email);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();
    }

    class PlaceOrderTask extends AsyncTask<String, Void, Void>
    {

        Order order;
        OrderLineItem orderLineItem;
        List<Product> products;
        List<OrderLineItem> orderLineItems;
        List<Integer> productIds, quantities;
        String orderId;
        int totalPrice, orderNumber;
        Calendar calendar;
        DateFormat dateDisplayFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
        String expectedDeliveryDate, orderedDate;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            order = new Order();
            orderLineItems = new ArrayList<>();
            productIds = new ArrayList<>();
            orderLineItems = new ArrayList<>();
            orderId = "";
            totalPrice = 0;
            calendar = Calendar.getInstance();
            orderedDate = dateDisplayFormat.format(calendar.getTime());
            calendar.add(Calendar.DATE, 3);
            expectedDeliveryDate = dateDisplayFormat.format(calendar.getTime());
        }

        @Override
        protected Void doInBackground(String... email)
        {

            productIds = getArguments().getIntegerArrayList("product_ids");
            quantities = getArguments().getIntegerArrayList("quantities");
            Log.d("PRODUCT_ID", "" + productIds.size());
            Log.d("PRODUCT_quantities", "" + quantities.size());

            orderNumber = orderPref.getInt(ZohoKartSharePreferences.ORDER_COUNT, 1);

            orderId = Order.ORDER_KEY + orderNumber;

            orderEditor = orderPref.edit();
            orderEditor.putInt(ZohoKartSharePreferences.ORDER_COUNT, orderNumber + 1);
            orderEditor.apply();

            products = zohokartDAO.getProductsForProductIds(productIds);
            if (products.size() > 0)
            {
                for (Product product : products)
                {
                    totalPrice += (product.getPrice() * quantities.get(products.indexOf(product)));
                }
                for (Integer productId : productIds)
                {
                    orderLineItem = new OrderLineItem();

                    orderLineItem.setOrderId(orderId);
                    orderLineItem.setProductId(productId);
                    orderLineItem.setQuantity(quantities.get(productIds.indexOf(productId)));
                    orderLineItems.add(orderLineItem);
                }
            }
            Log.d("ORDER_LINE_ITEMS", "" + orderLineItems.size());
            zohokartDAO.addOrderLineItems(orderLineItems);
            orderLineItems.clear();

            order.setId(orderId);
            order.setOrderedDate(orderedDate);
            order.setExpectedDeliveryDate(expectedDeliveryDate);
            order.setNumberOfProducts(products.size());
            order.setTotalPrice(totalPrice);
            order.setOrderStatus(Order.ORDER_PROCESSING);

            zohokartDAO.addOrder(order, email[0]);

            zohokartDAO.removeProductsInCart(email[0]);

            setAlarmForDelivery(orderNumber, order);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            totalPrice = 0;
            products.clear();
            productIds.clear();
            orderLineItems.clear();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("");
            alertDialogBuilder.setMessage("Order placed successfully");

            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    communicator.saveAndCloseCheckout();
                }
            });

            alertDialogBuilder.show();

        }
    }

    public interface PaymentCommunicator
    {
        void saveAndCloseCheckout();
    }

    private void setAlarmForDelivery(int orderNumber, Order order)
    {
        deliveryServiceCalendar.add(Calendar.SECOND, 30);
        Intent deliveryServiceIntent = new Intent(getActivity(), OrderDeliveryService.class);
        deliveryServiceIntent.putExtra("order", order);
        orderDeliveryService = PendingIntent.getService(getActivity(), orderNumber, deliveryServiceIntent, 0);
        alarmManager.set(AlarmManager.RTC, deliveryServiceCalendar.getTimeInMillis(), orderDeliveryService);
    }

}
