/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.axalent.view.material;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.axalent.R;

public class DialogMaterial extends android.app.Dialog {

    Context context;
    View view;
    View backView;
    LinearLayout mBodyView;
    String message;
    TextView messageTextView;
    String title;
    TextView titleTextView;
    ProgressBarCircularIndeterminate progressView;

    ButtonFlatMaterial buttonAccept;
    ButtonFlatMaterial buttonCancel;

    String buttonCancelText, buttonAcceptText;

    View.OnClickListener onAcceptButtonClickListener;
    View.OnClickListener onCancelButtonClickListener;

    View bodyView;
    boolean showProgress = false;
    boolean cancelableOnTouchOutside = true;
    boolean isGateway = false;
    int color;

    public DialogMaterial(Context context, String title, String message) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;// init Context
        this.message = message;
        this.title = title;
    }

    public DialogMaterial(Context context, String title, String message, boolean isGateway, int color) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;// init Context
        this.message = message;
        this.title = title;
        this.isGateway = isGateway;
        this.color = color;
    }

    public DialogMaterial(Context context, int customView) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;// init Context
    }

    public void addCancelButton(String buttonCancelText) {
        this.buttonCancelText = buttonCancelText;
    }

    public void setBodyView(View v) {
        this.bodyView = v;

        if (mBodyView != null) {
            mBodyView.removeAllViews();
            mBodyView.addView(bodyView);
        }
    }

    public void setShowProgress(boolean progress) {
        showProgress = progress;

        if (progressView != null) {
            progressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        }

    }

    public void setCanceledOnTouchOutside(boolean cancelable) {
        cancelableOnTouchOutside = cancelable;
    }

    public void addCancelButton(String buttonCancelText, View.OnClickListener onCancelButtonClickListener) {
        this.buttonCancelText = buttonCancelText;
        this.onCancelButtonClickListener = onCancelButtonClickListener;
    }

    public void addAcceptButton(String buttonAcceptText, View.OnClickListener onAcceptButtonClickListener) {
        this.buttonAcceptText = buttonAcceptText;
        this.onAcceptButtonClickListener = onAcceptButtonClickListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);

        mBodyView = (LinearLayout) findViewById(R.id.body);
        view = (RelativeLayout) findViewById(R.id.contentDialog);
        backView = (RelativeLayout) findViewById(R.id.dialog_rootView);
        progressView = (ProgressBarCircularIndeterminate) findViewById(R.id.progress);

        backView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < view.getLeft()
                        || event.getX() > view.getRight()
                        || event.getY() > view.getBottom()
                        || event.getY() < view.getTop()) {
                    if (cancelableOnTouchOutside) {
                        dismiss();
                    }
                }
                return false;
            }
        });

        this.titleTextView = (TextView) findViewById(R.id.title);
        this.messageTextView = (TextView) findViewById(R.id.message);
        setTitle(title);
        if (isGateway) {
            setMessageColor(message, color);
        } else {
            setMessage(message);
        }

        this.buttonAccept = (ButtonFlatMaterial) findViewById(R.id.button_accept);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onAcceptButtonClickListener != null) {
                    onAcceptButtonClickListener.onClick(v);
                }
                else {
                    dismiss();
                }
            }
        });

        if (buttonCancelText != null) {
            this.buttonCancel = (ButtonFlatMaterial) findViewById(R.id.button_cancel);
            this.buttonCancel.setVisibility(View.VISIBLE);
            this.buttonCancel.setText(buttonCancelText);
            buttonCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (onCancelButtonClickListener != null) {
                        onCancelButtonClickListener.onClick(v);
                    }
                    else {
                        dismiss();
                    }
                }
            });
        }

        if (buttonAcceptText != null) {
            this.buttonAccept = (ButtonFlatMaterial) findViewById(R.id.button_accept);
            this.buttonAccept.setVisibility(View.VISIBLE);
            this.buttonAccept.setText(buttonAcceptText);
            buttonAccept.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (onAcceptButtonClickListener != null) {
                        onAcceptButtonClickListener.onClick(v);
                    }
                    else {
                        dismiss();
                    }
                }
            });
        }

        if (bodyView != null) {
            mBodyView.addView(bodyView);
        }

        setShowProgress(showProgress);
    }

    @Override
    public void show() {
        super.show();
        // set dialog enter animations
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_main_show_amination));
        backView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_root_show_amin));
    }

    // GETERS & SETTERS

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        messageTextView.setText(message);

        messageTextView.setVisibility(message == null ? View.GONE : View.VISIBLE);
    }

    public void setMessageColor(String message, int color) {
        this.message = message;
        messageTextView.setText(message);
        messageTextView.setTextColor(color);

        messageTextView.setVisibility(message == null ? View.GONE : View.VISIBLE);
    }

    public TextView getMessageTextView() {
        return messageTextView;
    }

    public void setMessageTextView(TextView messageTextView) {
        this.messageTextView = messageTextView;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if (title == null) {
            titleTextView.setVisibility(View.GONE);
        }
        else {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
        }
    }

    public void setTitle(String title, int color) {
        this.title = title;
        if (title == null) {
            titleTextView.setVisibility(View.GONE);
        }
        else {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
            titleTextView.setTextColor(color);
        }
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public void setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
    }

    public ButtonFlatMaterial getButtonAccept() {
        return buttonAccept;
    }

    public void setButtonAccept(ButtonFlatMaterial buttonAccept) {
        this.buttonAccept = buttonAccept;
    }

    public ButtonFlatMaterial getButtonCancel() {
        return buttonCancel;
    }

    public void setButtonCancel(ButtonFlatMaterial buttonCancel) {
        this.buttonCancel = buttonCancel;
    }

    public void setOnAcceptButtonClickListener(
            View.OnClickListener onAcceptButtonClickListener) {
        this.onAcceptButtonClickListener = onAcceptButtonClickListener;
        if (buttonAccept != null) {
            buttonAccept.setOnClickListener(onAcceptButtonClickListener);
        }
    }

    public void setOnCancelButtonClickListener(
            View.OnClickListener onCancelButtonClickListener) {
        this.onCancelButtonClickListener = onCancelButtonClickListener;
        if (buttonCancel != null) {
            buttonCancel.setOnClickListener(onCancelButtonClickListener);
        }
    }

    @Override
    public void dismiss() {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.dialog_main_hide_amination);
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        DialogMaterial.super.dismiss();
                    }
                });

            }
        });
        Animation backAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_root_hide_amin);

        view.startAnimation(anim);
        backView.startAnimation(backAnim);
    }


}
