package com.wonder.musicapp.View;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wonder.musicapp.R;

/**
 * Created by Administrator on 2016/2/18.
 */
public class InputEditText extends LinearLayout {

    public InputEditText(Context context) {
        super(context);
        this.context = context;
        initView();
        addView(view);
    }

    public InputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
        addView(view);
    }

    private Context context;
    private ImageView ivDeleteText;
    private EditText etSearch;
    private Button btn_search;
    private View view;

    private void initView() {
        setOrientation(HORIZONTAL);
        view = LayoutInflater.from(context).inflate(R.layout.et_input, null);
        ivDeleteText = (ImageView) view.findViewById(R.id.ivDeleteText);
        etSearch = (EditText) view.findViewById(R.id.etSearch);
        btn_search= (Button) view.findViewById(R.id.btn_search);
        ivDeleteText.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                etSearch.setText("");
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    ivDeleteText.setVisibility(View.GONE);
                } else {
                    ivDeleteText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void setSearchClickListener(OnClickListener listener){
        btn_search.setOnClickListener(listener);
    }

    public String getText(){
        return etSearch.getText().toString();
    }
}
