package com.akaeid.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView display;
    private TextView expression;

    private String current = "0";
    private String operator = "";
    private String previous = "";
    private boolean newNumber = true;
    private boolean hasError = false;

    private int bg = Color.rgb(15, 17, 23);
    private int panel = Color.rgb(25, 28, 37);
    private int numberColor = Color.rgb(45, 49, 62);
    private int textColor = Color.WHITE;
    private int muted = Color.rgb(160, 167, 185);
    private int accent = Color.rgb(91, 105, 255);
    private int operatorColor = Color.rgb(42, 47, 67);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        getWindow().getDecorView().setSystemUiVisibility(0);

        createUI();
        updateDisplay();
    }

    private void createUI() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(14), dp(20), dp(18));
        root.setBackgroundColor(bg);

        LinearLayout.LayoutParams rootParams =
                new LinearLayout.LayoutParams(
                        -1, -1
                );

        setContentView(root, rootParams);

        TextView title = new TextView(this);
        title.setText("CALCULATOR");
        title.setTextColor(muted);
        title.setTextSize(13);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setLetterSpacing(0.15f);

        root.addView(title, new LinearLayout.LayoutParams(
                -1, dp(42)
        ));

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.BOTTOM | Gravity.END);
        screen.setPadding(dp(8), dp(20), dp(8), dp(24));

        LinearLayout.LayoutParams screenParams =
                new LinearLayout.LayoutParams(-1, 0, 1.0f);

        root.addView(screen, screenParams);

        expression = new TextView(this);
        expression.setTextColor(muted);
        expression.setTextSize(18);
        expression.setGravity(Gravity.END);
        expression.setSingleLine(true);
        expression.setText("");

        screen.addView(expression, new LinearLayout.LayoutParams(
                -1, dp(40)
        ));

        display = new TextView(this);
        display.setTextColor(textColor);
        display.setTextSize(48);
        display.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        display.setSingleLine(true);
        display.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);

        screen.addView(display, new LinearLayout.LayoutParams(
                -1, dp(90)
        ));

        LinearLayout divider = new LinearLayout(this);
        divider.setBackgroundColor(Color.rgb(47, 51, 65));

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(-1, dp(1));

        dividerParams.bottomMargin = dp(16);
        root.addView(divider, dividerParams);

        String[][] keys = {
                {"AC", "DEL", "%", "÷"},
                {"7", "8", "9", "×"},
                {"4", "5", "6", "−"},
                {"1", "2", "3", "+"},
                {"±", "0", ".", "="}
        };

        LinearLayout keypad = new LinearLayout(this);
        keypad.setOrientation(LinearLayout.VERTICAL);

        root.addView(keypad, new LinearLayout.LayoutParams(
                -1, 0, 1.35f
        ));

        for (String[] rowKeys : keys) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams rowParams =
                    new LinearLayout.LayoutParams(-1, 0, 1f);

            rowParams.bottomMargin = dp(9);
            keypad.addView(row, rowParams);

            for (String key : rowKeys) {
                Button button = new Button(this);
                button.setText(key);
                button.setTextSize(
                        key.length() > 2 ? 17 : 24
                );
                button.setTextColor(textColor);
                button.setAllCaps(false);
                button.setPadding(0, 0, 0, 0);
                button.setMinWidth(0);
                button.setMinimumWidth(0);
                button.setMinHeight(0);
                button.setMinimumHeight(0);
                button.setStateListAnimator(null);
                button.setTypeface(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                );

                int color = getButtonColor(key);
                button.setBackground(makeBackground(
                        color, key.equals("=") ? 22 : 20
                ));

                LinearLayout.LayoutParams buttonParams =
                        new LinearLayout.LayoutParams(0, -1, 1f);

                buttonParams.leftMargin = dp(4);
                buttonParams.rightMargin = dp(4);

                row.addView(button, buttonParams);

                button.setOnClickListener(v -> onKey(key));
            }
        }

        TextView footer = new TextView(this);
        footer.setText("Simple • Fast • Offline");
        footer.setTextColor(muted);
        footer.setTextSize(12);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(4), 0, 0);

        root.addView(footer, new LinearLayout.LayoutParams(
                -1, dp(28)
        ));
    }

    private int getButtonColor(String key) {
        if (key.equals("=")) {
            return accent;
        }

        if (key.equals("AC") || key.equals("DEL")
                || key.equals("%") || key.equals("±")) {
            return operatorColor;
        }

        if (key.equals("÷") || key.equals("×")
                || key.equals("−") || key.equals("+")) {
            return Color.rgb(49, 54, 82);
        }

        return numberColor;
    }

    private GradientDrawable makeBackground(
            int color, int radius
    ) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private void onKey(String key) {
        if (key.equals("AC")) {
            clearAll();
            return;
        }

        if (key.equals("DEL")) {
            deleteLast();
            return;
        }

        if (key.equals("%")) {
            percentage();
            return;
        }

        if (key.equals("±")) {
            toggleSign();
            return;
        }

        if (key.equals("=")) {
            calculateResult();
            return;
        }

        if (isOperator(key)) {
            chooseOperator(key);
            return;
        }

        if (key.equals(".")) {
            addDecimal();
            return;
        }

        addDigit(key);
    }

    private boolean isOperator(String key) {
        return key.equals("+") || key.equals("−")
                || key.equals("×") || key.equals("÷");
    }

    private void addDigit(String digit) {
        if (hasError) clearAll();

        if (newNumber) {
            current = digit;
            newNumber = false;
        } else if (current.equals("0")) {
            current = digit;
        } else if (current.length() < 15) {
            current += digit;
        }

        updateDisplay();
    }

    private void addDecimal() {
        if (hasError) clearAll();

        if (newNumber) {
            current = "0.";
            newNumber = false;
        } else if (!current.contains(".")) {
            current += ".";
        }

        updateDisplay();
    }

    private void chooseOperator(String nextOperator) {
        if (hasError) return;

        if (!operator.isEmpty() && !newNumber) {
            calculateResult();

            if (hasError) return;
        }

        previous = current;
        operator = nextOperator;
        newNumber = true;

        expression.setText(
                format(previous) + " " + operator
        );

        updateDisplay();
    }

    private void calculateResult() {
        if (operator.isEmpty() || previous.isEmpty()
                || newNumber || hasError) {
            return;
        }

        try {
            BigDecimal a = new BigDecimal(previous);
            BigDecimal b = new BigDecimal(current);
            BigDecimal result;

            switch (operator) {
                case "+":
                    result = a.add(b);
                    break;

                case "−":
                    result = a.subtract(b);
                    break;

                case "×":
                    result = a.multiply(b);
                    break;

                case "÷":
                    if (b.compareTo(BigDecimal.ZERO) == 0) {
                        showError();
                        return;
                    }

                    result = a.divide(
                            b, MathContext.DECIMAL128
                    );
                    break;

                default:
                    return;
            }

            expression.setText(
                    format(previous) + " " + operator
                    + " " + format(current) + " ="
            );

            current = result.stripTrailingZeros()
                    .toPlainString();

            if (current.length() > 15) {
                current = result.round(
                        new MathContext(12)
                ).stripTrailingZeros().toPlainString();
            }

            operator = "";
            previous = "";
            newNumber = true;

            updateDisplay();

        } catch (Exception e) {
            showError();
        }
    }

    private void percentage() {
        if (hasError) return;

        try {
            BigDecimal value = new BigDecimal(current);
            value = value.divide(BigDecimal.valueOf(100));

            current = value.stripTrailingZeros()
                    .toPlainString();

            newNumber = false;
            updateDisplay();

        } catch (Exception e) {
            showError();
        }
    }

    private void toggleSign() {
        if (hasError) return;

        if (current.startsWith("-")) {
            current = current.substring(1);
        } else if (!current.equals("0")) {
            current = "-" + current;
        }

        updateDisplay();
    }

    private void deleteLast() {
        if (hasError) {
            clearAll();
            return;
        }

        if (newNumber) {
            current = "0";
            newNumber = false;
        } else if (current.length() > 1) {
            current = current.substring(
                    0, current.length() - 1
            );

            if (current.equals("-") || current.isEmpty()) {
                current = "0";
            }
        } else {
            current = "0";
        }

        updateDisplay();
    }

    private void clearAll() {
        current = "0";
        previous = "";
        operator = "";
        newNumber = true;
        hasError = false;

        expression.setText("");
        updateDisplay();
    }

    private void showError() {
        current = "Error";
        previous = "";
        operator = "";
        newNumber = true;
        hasError = true;

        expression.setText("Invalid calculation");
        updateDisplay();
    }

    private String format(String value) {
        try {
            BigDecimal number = new BigDecimal(value);
            return number.stripTrailingZeros().toPlainString();
        } catch (Exception e) {
            return value;
        }
    }

    private void updateDisplay() {
        display.setText(current);

        if (current.length() > 10) {
            display.setTextSize(34);
        } else if (current.length() > 7) {
            display.setTextSize(40);
        } else {
            display.setTextSize(48);
        }
    }

    private int dp(float value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics().density + 0.5f
        );
    }
}
