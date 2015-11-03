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
import com.muhil.zohokart.utils.CardValidator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavedCardFragment extends android.support.v4.app.Fragment
{

    View savedCardFragment;
    EditText cardNumber, nameOnCard;
    TextView cardTypeTextView;
    Spinner expiryMonthSpinner, expiryYearSpinner;
    Calendar calendar;
    String[] months = new String[12], years = new String[8];
    String cardNumberString, cardType;
    String nameOncardText;
    String expiryDate;
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
        for (int i = 1; i <= 12; i++)
        {
            months[i-1] = String.valueOf(i);
        }
        for (int i = 0; i < years.length; i++)
        {
            years[i] = String.valueOf(calendar.get(Calendar.YEAR) + i);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        savedCardFragment = inflater.inflate(R.layout.fragment_saved_card, container, false);

        expiryMonthSpinner = (Spinner) savedCardFragment.findViewById(R.id.expiry_month);
        expiryYearSpinner = (Spinner) savedCardFragment.findViewById(R.id.expiry_year);
        cardNumber = (EditText) savedCardFragment.findViewById(R.id.card_number);
        nameOnCard = (EditText) savedCardFragment.findViewById(R.id.name_on_card);
        cardTypeTextView = (TextView) savedCardFragment.findViewById(R.id.card_type);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expiryMonthSpinner.setAdapter(monthAdapter);
        ArrayAdapter<String> yearAdapater = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, years);
        yearAdapater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expiryYearSpinner.setAdapter(yearAdapater);

        cardNumber.addTextChangedListener(
                new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
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

        (savedCardFragment.findViewById(R.id.save_card_action)).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        PaymentCard paymentCard = new PaymentCard();

                        cardNumberString = cardNumber.getText().toString();
                        cardType = cardTypeTextView.getText().toString();
                        nameOncardText = nameOnCard.getText().toString();
                        expiryDate = String.valueOf(expiryMonthSpinner.getSelectedItem()) + "/" + String.valueOf(expiryYearSpinner.getSelectedItem());

                        if (cardNumberString.length() != 0 && cardNumberString.length() == 16)
                        {
                            if (!(CardValidator.ifAnyLetterExist(cardNumberString)))
                            {
                                if ((Integer.parseInt(cardNumberString.substring(0,2)) >= 40 && Integer.parseInt(cardNumberString.substring(0,2)) <= 49) ||
                                        (Integer.parseInt(cardNumberString.substring(0,2)) >= 50 && Integer.parseInt(cardNumberString.substring(0,2)) <= 59))
                                {
                                    if (!(CardValidator.ifAnyDigitExist(nameOncardText)))
                                    {
                                        paymentCard.setEmail(getArguments().getString(Account.EMAIL));
                                        paymentCard.setCardNumber(cardNumberString);
                                        paymentCard.setNameOnCard(nameOncardText);
                                        if ((Integer.parseInt(cardNumberString.substring(0,2)) >= 40 && Integer.parseInt(cardNumberString.substring(0,2)) <= 49))
                                        {
                                            paymentCard.setCardType("Visa");
                                        }
                                        else if ((Integer.parseInt(cardNumberString.substring(0,2)) >= 50 && Integer.parseInt(cardNumberString.substring(0,2)) <= 59))
                                        {
                                            paymentCard.setCardType("Mastercard");
                                        }
                                        paymentCard.setExpiryDate(expiryDate);
                                        communicator.sendCard(paymentCard);
                                        getActivity().getSupportFragmentManager().popBackStack();
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
                            Toast.makeText(getActivity(), "Card Number too small.", Toast.LENGTH_SHORT).show();
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



    public interface PaymentCardCommunicator
    {
        void sendCard(PaymentCard paymentCard);
    }

}
