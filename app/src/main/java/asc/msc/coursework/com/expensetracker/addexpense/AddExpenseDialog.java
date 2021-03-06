package asc.msc.coursework.com.expensetracker.addexpense;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

import asc.msc.coursework.com.expensetracker.MainActivity;
import asc.msc.coursework.com.expensetracker.R;
import asc.msc.coursework.com.expensetracker.dao.DataManipulation;
import asc.msc.coursework.com.expensetracker.dto.Category;
import asc.msc.coursework.com.expensetracker.dto.Expense;
import asc.msc.coursework.com.expensetracker.dto.Income;
import asc.msc.coursework.com.expensetracker.util.Util;
import asc.msc.coursework.com.expensetracker.R;

public class AddExpenseDialog extends DialogFragment {

    public static final String NAME = "name";
    public static final String DATE = "date";
    public static final String COMMENT = "comment";
    public static final String VLAUE = "value";
    public static final String CATEGORY = "category";
    public static final String SOURCE = "source";
    public static final String POSITION = "position";

    Util util = new Util();
    DataManipulation dataManipulation = new DataManipulation();
    private String current = "";

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    public AddExpenseDialog() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(onClickListener);
        ExpenseListDropDown expenseListDropDown = new ExpenseListDropDown();
        Spinner category = (Spinner) view.findViewById(R.id.category);
        expenseListDropDown.createDropDown(category, getContext(), dataManipulation.getCategories());
        addExpenseOnClickListner(view);

    }

    /**
     * All the logic's after clicking the add button goes here.
     *
     * @param view
     */
    private void addExpenseOnClickListner(@NonNull final View view) {
        View addExpense = view.findViewById(R.id.add_expense);
        final TextView expenseNameView = (TextView) view.findViewById(R.id.expenseName);
        final TextView expenseDetailsView = (TextView) view.findViewById(R.id.expenseDetails);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        final RadioGroup transactionType = (RadioGroup) view.findViewById(R.id.transactionType);
        final RadioButton incomeRadio = (RadioButton) view.findViewById(R.id.incomeRadio);
        final RadioButton expenseRadio = (RadioButton) view.findViewById(R.id.expenseRadio);
        final EditText value = (EditText) view.findViewById(R.id.value);
        final Spinner categorySpinner = (Spinner) view.findViewById(R.id.category);

        value.setRawInputType(Configuration.KEYBOARD_12KEY);

        value.addTextChangedListener(new TextWatcher() {
            DecimalFormat dec = new DecimalFormat("0.00");

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    value.removeTextChangedListener(this);

                    String formatted = util.getDollerConvertedString(s);

                    current = formatted;
                    value.setText(formatted);
                    value.setSelection(formatted.length());

                    value.addTextChangedListener(this);
                }
            }
        });

        if (getArguments() != null) {
            if (getArguments().getString(NAME) != null)
                expenseNameView.setText(getArguments().getString(NAME));
            if (getArguments().getString(COMMENT) != null)
                expenseDetailsView.setText(getArguments().getString(COMMENT));
            ArrayList<Integer> integerArrayList = getArguments().getIntegerArrayList(DATE);
            if (!String.valueOf(integerArrayList).isEmpty()) {
                datePicker.updateDate(integerArrayList.get(2), integerArrayList.get(1), integerArrayList.get(0));
            }
            if (!getArguments().getString(VLAUE).isEmpty() || (getArguments().getString(VLAUE) != null)) {
                value.setText(getArguments().getString(VLAUE));
            }

            if (getArguments().getInt(CATEGORY) != -1) {
                int category = getArguments().getInt(CATEGORY);
                categorySpinner.setSelection(category);
                expenseRadio.toggle();
            } else {
                int source = getArguments().getInt(SOURCE);
                categorySpinner.setSelection(source);
                incomeRadio.toggle();

            }
        }
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = expenseNameView.getText().toString();
                String details = expenseDetailsView.getText().toString();

                ArrayList<Integer> date = util.getDate(datePicker.getDayOfMonth(), datePicker.getMonth(), datePicker.getYear());
                BigDecimal enteredValue = new BigDecimal(current.replace("$", "").replace(",", ""));
                int selectedItemPosition = categorySpinner.getSelectedItemPosition();

                if (transactionType.getCheckedRadioButtonId() == incomeRadio.getId()) {
                    if (getArguments() != null)
                        dataManipulation.addTransaction(new Income(name, details, date, enteredValue, selectedItemPosition), getArguments().getInt(POSITION));
                    else
                        dataManipulation.addTransaction(new Income(name, details, date, enteredValue, selectedItemPosition));
                } else {
                    if (getArguments() != null)
                        dataManipulation.addTransaction(new Expense(name, details, date, enteredValue, selectedItemPosition), getArguments().getInt(POSITION));
                    else
                        dataManipulation.addTransaction(new Expense(name, details, date, enteredValue, selectedItemPosition));

                }
                MainActivity.expenseList.setArrayList(dataManipulation.getTransactions());
                MainActivity.expenseList.notifyDataSetChanged();
                dismiss();
            }
        });
    }




}
