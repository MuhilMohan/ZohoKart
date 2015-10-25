package com.muhil.zohokart.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.PaymentCard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavedCardFragment extends android.support.v4.app.Fragment
{

    View savedCardFragment;
    EditText cardNumberPart1, cardNumberPart2, cardNumberPart3, cardNumberPart4, nameOnCard;
    TextView cardTypeTextView;
    NumberPicker monthPicker, yearPicker;
    Spinner cardTypeSpinner;
    Calendar calendar;
    String[] cardTypes;
    String cardNumber="", cardType, nameOncardText, expiryDate;
    PaymentCardCommunicator communicator;

    public static SavedCardFragment getInstance(String email)
    {
        SavedCardFragment savedCardFragment = new SavedCardFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Account.EMAIL, email);
        savedCardFragment.setArguments(bundle);
        return savedCardFragment;
    }

    public SavedCardFragment()
    {
        // Required empty public constructor
    }

    public void setCommunicator(PaymentCardCommunicator communicator)
    {
        this.communicator = communicator;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        savedCardFragment = inflater.inflate(R.layout.fragment_saved_card, container, false);

        monthPicker = (NumberPicker) savedCardFragment.findViewById(R.id.validity_month);
        yearPicker = (NumberPicker) savedCardFragment.findViewById(R.id.validity_year);
        cardNumberPart1 = (EditText) savedCardFragment.findViewById(R.id.card_number_part1);
        cardNumberPart2 = (EditText) savedCardFragment.findViewById(R.id.card_number_part2);
        cardNumberPart3 = (EditText) savedCardFragment.findViewById(R.id.card_number_part3);
        cardNumberPart4 = (EditText) savedCardFragment.findViewById(R.id.card_number_part4);
        nameOnCard = (EditText) savedCardFragment.findViewById(R.id.name_on_card);
        cardTypeTextView = (TextView) savedCardFragment.findViewById(R.id.card_type);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(calendar.get(Calendar.MONTH) + 1);
        yearPicker.setMinValue(calendar.get(Calendar.YEAR));
        yearPicker.setMaxValue(calendar.get(Calendar.YEAR) + 7);
        cardTypes = getResources().getStringArray(R.array.cart_types);

        cardNumberPart1.addTextChangedListener(
                new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
                        if (s.length() == 4)
                        {
                            cardNumberPart2.requestFocus();
                        }
                        if (s.length() == 2)
                        {
                            if (Integer.parseInt(String.valueOf(s.subSequence(0, 2))) >= 40 && Integer.parseInt(String.valueOf(s.subSequence(0, 2))) <= 49)
                            {
                                cardTypeTextView.setText("Visa");
                            }
                            else if (Integer.parseInt(String.valueOf(s.subSequence(0, 2))) >= 51 && Integer.parseInt(String.valueOf(s.subSequence(0, 2))) <= 55)
                            {
                                cardTypeTextView.setText("Mastercard");
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s)
                    {
                    }
                }
        );

        cardNumberPart2.addTextChangedListener(
                new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
                        if (s.length() == 4)
                        {
                            cardNumberPart3.requestFocus();
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s)
                    {}
                }
        );

        cardNumberPart3.addTextChangedListener(
                new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
                        if (s.length() == 4)
                        {
                            cardNumberPart4.requestFocus();
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s)
                    {}
                }
        );

        cardNumberPart4.addTextChangedListener(
                new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
                        if (count == 4)
                        {
                            cardNumberPart4.clearFocus();
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s)
                    {}
                }
        );

        (savedCardFragment.findViewById(R.id.save_card_action)).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        PaymentCard paymentCard = new PaymentCard();
                        List<String> cardNumberParts = new ArrayList<>();
                        cardNumberParts.add(cardNumberPart1.getText().toString());
                        cardNumberParts.add(cardNumberPart2.getText().toString());
                        cardNumberParts.add(cardNumberPart3.getText().toString());
                        cardNumberParts.add(cardNumberPart4.getText().toString());
                        for (String cardNumberPart : cardNumberParts)
                        {
                            if (!checkForAnyLetter(cardNumberPart))
                            {
                                cardNumber = cardNumber + cardNumberPart;
                            }
                            else
                            {
                                cardNumber = "";
                                break;
                            }
                        }

                        if (cardNumber.length() != 0 && cardNumber.length() == 16)
                        {
                            cardType = cardTypeTextView.getText().toString();
                            nameOncardText = nameOnCard.getText().toString();
                            expiryDate = monthPicker.getValue() + "/" + yearPicker.getValue();
                            if (!(ifAnyDigitExist(nameOncardText)))
                            {
                                paymentCard.setEmail(getArguments().getString(Account.EMAIL));
                                paymentCard.setCardNumber(cardNumber);
                                paymentCard.setNameOnCard(nameOncardText);
                                paymentCard.setCardType(cardType);
                                paymentCard.setExpiryDate(expiryDate);
                                communicator.sendCard(paymentCard);
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                            else
                            {
                                Toast.makeText(getActivity(), "Enter a valid name.", Toast.LENGTH_SHORT).show();
                                cardNumber = "";
                            }
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Enter a valid card number.", Toast.LENGTH_SHORT).show();
                            cardNumber = "";
                        }
                    }
                }
        );

        (savedCardFragment.findViewById(R.id.cancel_action)).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
        );

        return savedCardFragment;
    }

    private boolean checkForAnyLetter(String partOfCardNumber)
    {
        boolean result = false;
        char[] cardNumberArray = partOfCardNumber.toCharArray();
        for (int i = 0; i < cardNumberArray.length; i++)
        {
            if ((Integer.parseInt(String.valueOf(cardNumberArray[i])) >= 0 && Integer.parseInt(String.valueOf(cardNumberArray[i])) <= 9))
            {
                result = false;
            }
            else
            {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean ifAnyDigitExist(String nameOnCard)
    {
        boolean result = false;
        char[] nameOnCardArray = nameOnCard.toCharArray();
        for (int i = 0; i < nameOnCardArray.length; i++)
        {
            if ((nameOnCardArray[i] <= 'A' || nameOnCardArray[i] >= 'Z'))
            {
                if (String.valueOf(nameOnCardArray[i]).equals(" "))
                {
                    result = false;
                }
                else
                {
                    result = true;
                    break;
                }
            }
            else
            {
                result = false;
            }
        }
        return result;
    }

    public interface PaymentCardCommunicator
    {
        void sendCard(PaymentCard paymentCard);
    }

}
