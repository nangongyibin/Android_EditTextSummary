package com.ngyb.edittextsummary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：南宫燚滨
 * 描述：
 * 邮箱：nangongyibin@gmail.com
 * 日期：2020/2/15 10:17
 */
public class DropEditText extends FrameLayout implements View.OnClickListener ,View.OnFocusChangeListener, TextWatcher{
    private static final String TAG = "DropEditText";
    private final Context context;
    private final AttributeSet attrs;
    private final int defStyleAttr;
    private WrapListView popView;
    private EditText edittext;
    private PopupWindow popup;
    private OnFocusChangeListener onFocusChangeListener;
    private TextWatcher textWatcher;
    private OnClickListener onClickListener;
    private OnListChange onListChange;
    private DropEditTextAdapter adapter;
    public List<String> list = new ArrayList<>();
    //默认配置
    public static final int TYPE_DEFAULT = 0;
    public static final int MAXLENGTH_DEFAULT = 9;
    public static final int TEXTSIZE_DEFAULT = 16;
    public static final int TEXTCOLOR_DEFAULT = Color.BLACK;
    public static final boolean ENABLE_DEFAULT = true;
    //下方是xml配置
    private int type;//0是文本 1是数字
    private int maxlength;
    private String hint;
    private String text;
    private int textsize;
    private int textColor;
    private String digits;
    private boolean enable = ENABLE_DEFAULT;
    //下方的是过程中的
    public String currentValue = "";
    public String historyValue = "";
    private String maxRange = "";
    private String minRange = "";
    private Double max, min, doubleValue;
    private String before;
    private int dot = -1;
    private int localDot;//本地中用来计算小数点个数的
    private boolean isOrNot = false;

    public DropEditText(@NonNull Context context) {
        this(context, null);
    }

    public DropEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public DropEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.defStyleAttr = defStyleAttr;
        initOneStep();
    }

    private void initOneStep() {
        Log.e(TAG, "initOneStep: ");
        initLayout();
        initDropEditText();
        initAttrs();
    }

    private void initAttrs() {
        Log.e(TAG, "initAttrs: "+(context ==null) );
        Log.e(TAG, "initAttrs: "+(attrs ==null) );
        Log.e(TAG, "initAttrs: "+(defStyleAttr ==-1) );
        if (context != null && attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DropEditText, defStyleAttr, 0);
            type = ta.getInt(R.styleable.DropEditText_inputType, TYPE_DEFAULT);
            Log.e(TAG, "initAttrs: "+type );
            maxlength = ta.getInt(R.styleable.DropEditText_maxLength, MAXLENGTH_DEFAULT);
            hint = ta.getString(R.styleable.DropEditText_hint);
            text = ta.getString(R.styleable.DropEditText_text);
            textsize = ta.getDimensionPixelSize(R.styleable.DropEditText_textSize, numberToPx(context, TEXTSIZE_DEFAULT));
            textColor = ta.getColor(R.styleable.DropEditText_textColor, TEXTCOLOR_DEFAULT);
            digits = ta.getString(R.styleable.DropEditText_digits);
            enable = ta.getBoolean(R.styleable.DropEditText_enabled, ENABLE_DEFAULT);
            ta.recycle();
        }
    }

    /**
     * 默认值的数字转成px
     *
     * @param context
     * @param defaultValue
     * @return
     */
    public int numberToPx(Context context, float defaultValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (defaultValue * scale + 0.5f);
    }

    private void initDropEditText() {
        if (context != null) {
            popView = (WrapListView) LayoutInflater.from(context).inflate(R.layout.pop_view, null);
        }
    }

    private void initLayout() {
        if (context != null) {
            LayoutInflater.from(context).inflate(R.layout.drop_edittext_layout, this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initTwoStep();
        Log.e(TAG, "onFinishInflate: ");
    }

    private void initTwoStep() {
        initView();
        initXmlSetting();
        initListener();
        setTextColor();

    }

    private void initListener() {
        Log.e(TAG, "initListener: ");
        if (edittext != null) {
            Log.e(TAG, "initListener: 1");
            edittext.setOnClickListener(this);
            Log.e(TAG, "initListener: 2");
            edittext.setOnFocusChangeListener(this);
            Log.e(TAG, "initListener: 3");
            edittext.addTextChangedListener(this);
            Log.e(TAG, "initListener: 4");
        }
    }

    private void initXmlSetting() {
        if (digits != null && !digits.equals("")) {
            edittext.setKeyListener(DigitsKeyListener.getInstance(digits));
        }
        if (text != null && !text.equals("")) {
            edittext.setText(text);
        }
        if (maxlength >= 0) {
            edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxlength)});
        }
        if (hint != null && !hint.equals("")) {
            edittext.setHint(hint);
        }
        switch (type) {
            case 0:
                edittext.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                break;
            case 1:
                edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
        }
        if (textsize != 0) {
            edittext.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        }
        setDefaultTextColor();
        edittext.setEnabled(enable);
    }

    private void setDefaultTextColor() {
        if (textColor != 0xFF000000) {
            edittext.setTextColor(textColor);
        } else {
            edittext.setTextColor(TEXTCOLOR_DEFAULT);
        }
    }

    private void initView() {
        edittext = findViewById(R.id.drop_edittext);
    }

    /**
     * @param view
     * @param hasFocus    是否有焦点
     */
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        Log.e(TAG, "onFocusChange: ");
        if (onFocusChangeListener != null) {
            onFocusChangeListener.onFocusChange(view, hasFocus);
            return;
        }
        if (hasFocus) {
            initAdapter();
            showPopupWindow(view);
            setTextColor();
        } else {
            if (popup != null) {
                popup.dismiss();
            }
            historyValue = currentValue;
            //下面区分输入类型
            switch (type) {
                case 0:
                    currentValue = edittext.getText().toString().trim();
                    break;
                case 1:
                    getCurrentValue();
                    break;
            }
            if (historyValue != null && !historyValue.equals("")) {
                if (!historyValue.equals(currentValue)) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(0, historyValue);
                    initAdapter();
                    if (onListChange != null) {
                        onListChange.change(historyValue);
                    }
                    setTextColor();
                }
            }
        }
    }

    private void getCurrentValue() {
        String value = edittext.getText().toString().trim();
        if (value != null && !value.equals("")) {
            //判断输入的数是否是数
            if (isNumeric(value)) {
                doubleValue = Double.parseDouble(value);
                if (isNumeric(maxRange) && isNumeric(minRange)) {
                    max = Double.parseDouble(maxRange);
                    min = Double.parseDouble(minRange);
                    //在限制范围内，符合要求
                    if (doubleValue >= min && doubleValue <= max) {
                        currentValue = value;
                    } else {
                        currentValue = "";
                        setText(currentValue);
                        //不符合要求
                        if (onListChange != null) {
                            onListChange.sendMessage("输入的数必须在" + minRange + "---" + maxRange + "之间的数");
                        }
                    }
                } else {
                    currentValue = value;
                }
            } else {
                currentValue = "";
                setText(currentValue);
                //不符合要求
                if (onListChange != null) {
                    onListChange.sendMessage("输入的数不是一个数");
                }
            }
        }
    }

    /**
     * 判断是否是一个数
     *
     * @param str
     * @return
     */
    public boolean isNumeric(String str) {
        if (str != null && !str.equals("")&&str.matches("^[-+]?\\d+(\\.\\d+)?$")) {
            return true;
        } else {
            return false;
        }
    }

    private void setTextColor() {
        Log.e(TAG, "setTextColor: " );
        if (list != null && list.size() > 0) {
            edittext.setTextColor(Color.RED);
        } else {
            setDefaultTextColor();
        }
    }

    /**
     * 展示PopupWindow
     *
     * @param view
     */
    private void showPopupWindow(View view) {
        Log.e(TAG, "showPopupWindow: " );
        if (popup != null) {
            popup.showAsDropDown(view, 0, 5);
        }
    }

    private void initAdapter() {
        Log.e(TAG, "initAdapter: " );
        if (context != null) {
            if (adapter == null) {
                adapter = new DropEditTextAdapter(context);
                if (list == null) {
                    list = new ArrayList<>();
                }
                adapter.setData(list);
                if (popView != null) {
                    popView.setAdapter(adapter);
                    popup = new PopupWindow(popView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popup.setOutsideTouchable(true);
                }
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.e(TAG, "beforeTextChanged: " );
        if (textWatcher != null) {
            textWatcher.beforeTextChanged(charSequence, i, i1, i2);
            return;
        }
        //为了下面的不符合要求回退
        before = charSequence.toString();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.e(TAG, "onTextChanged: " );
        if (textWatcher != null) {
            textWatcher.onTextChanged(charSequence, i, i1, i2);
            return;
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.e(TAG, "afterTextChanged: " );
        if (textWatcher != null) {
            textWatcher.afterTextChanged(editable);
            return;
        }
        String values = editable.toString();
        switch (type) {
            case 1:
                if (values != null && !values.equals("")) {
                    if (!values.equals(".")) {
                        int radixPos = values.indexOf(".");//小数点的位置
                        if (dot < 0) {
                            if (radixPos <= 0) {
                                localDot = 0;
                            } else {
                                localDot = editable.length() - 1 - radixPos;
                            }
                        } else {
                            if (radixPos >= 0) {
                                if ((editable.length() - radixPos - 1) > dot) {
                                    String subStr = values.substring(0, dot + radixPos + 1);
                                    edittext.setText(subStr);
                                    if (onListChange != null) {
                                        edittext.setSelection(values.length());
                                        onListChange.sendMessage("此输入框只能输入" + dot + "长度的小数位数的数字，您输入的长度超过了此长度，谢谢");
                                    }
                                    return;
                                }
                                if ((editable.length() - radixPos - 1) == dot && dot == 0) {
                                    String subStr = values.substring(0, radixPos);
                                    edittext.setText(subStr);
                                    if (onListChange != null) {
                                        onListChange.sendMessage("设置的小数点个数为0，因此不能输入小数点！！");
                                    }
                                    return;
                                }
                            }
                        }
                        if (isNumeric(values)) {
                            doubleValue = Double.parseDouble(values);
                            if (isNumeric(maxRange) && isNumeric(minRange)) {
                                max = Double.parseDouble(maxRange);
                                min = Double.parseDouble(minRange);
                                if (doubleValue >= min) {
                                    if (doubleValue >= min && doubleValue <= max) {
                                        if (onListChange != null) {
                                            onListChange.newest(values);
                                        }
                                    } else {
                                        edittext.setText(before);
                                        if (onListChange != null) {
                                            onListChange.newest(before);
                                            onListChange.sendMessage("输入的数必须在" + minRange + "---" + maxRange + "之间的数");
                                        }
                                    }
                                } else {
                                    if (onListChange != null) {
                                        onListChange.newest(values);
                                    }
                                }
                            } else {
                                if (onListChange != null) {
                                    onListChange.newest(values);
                                }
                            }
                        } else {
                            if (onListChange != null) {
                                onListChange.newest(values);
                            }
                        }
                    } else {
                        edittext.setText(before);
                        if (onListChange != null) {
                            onListChange.newest(before);
                            onListChange.sendMessage("输入的数不能以.开始的");
                        }
                    }
                } else {
                    if (onListChange != null) {
                        onListChange.newest(values);
                    }
                }
                break;
            case 0:
                if (onListChange != null) {
                    onListChange.newest(values);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Log.e(TAG, "onClick: ");
        if (onClickListener != null && !isOrNot) {
            onClickListener.onClick(view);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            popView.setWidth(getMeasuredWidth());
        }
    }

    //下面是暴露出的方法
    public abstract static class OnFocusChangeListener {
        public abstract void onFocusChange(View view, boolean b);
    }

    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }

    public abstract static class TextWatcher {
        public abstract void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2);

        public abstract void onTextChanged(CharSequence charSequence, int i, int i1, int i2);

        public abstract void afterTextChanged(Editable editable);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
    }

    public abstract static class OnClickListener {
        public abstract void onClick(View var1);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public abstract static class OnListChange {
        public abstract void change(String value);

        public abstract void newest(String value);

        public void sendMessage(String message) {
        }
    }

    public void setOnListChange(OnListChange onListChange) {
        this.onListChange = onListChange;
    }

    public void setText(String str) {
        edittext.setText(str);
    }

    /**
     * 返回输入的数的小数点的个数
     *
     * @return
     */
    public int getLocalDot() {
        return localDot;
    }

    /**
     * 设置小数点
     *
     * @param dot
     */
    public void setDot(int dot) {
        this.dot = dot;
    }

    /**
     * 设置最大数
     *
     * @param maxRange
     */
    public void setMaxRange(String maxRange) {
        this.maxRange = maxRange;
    }

    /**
     * 设置最小值
     *
     * @param minRange
     */
    public void setMinRange(String minRange) {
        this.minRange = minRange;
    }

    /**
     * 获取输入框内的内容
     *
     * @return
     */
    public String getText() {
        return edittext.getText().toString().trim();
    }

    public void setEnable(boolean enable) {
        if (edittext != null) {
            edittext.setEnabled(enable);
        }
    }

    public void setClickNotEdit(boolean isOrNot) {
        this.isOrNot = isOrNot;
        if (edittext != null) {
            edittext.setFocusable(isOrNot);
        }
    }

    /**
     * 根据列表大小设置颜色
     *
     * @param lists
     */
    private void setTextColor(List<String> lists) {
        if (lists != null && lists.size() > 0) {
            edittext.setTextColor(Color.RED);
        } else {
            setDefaultTextColor();
        }
    }

    /**
     * 设置输入框的颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        if (edittext != null) {
            edittext.setTextColor(color);
        }
    }
}
