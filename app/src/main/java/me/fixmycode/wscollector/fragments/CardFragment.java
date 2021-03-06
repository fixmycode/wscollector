package me.fixmycode.wscollector.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.fixmycode.wscollector.R;
import me.fixmycode.wscollector.drawables.EffectDrawable;
import me.fixmycode.wscollector.wsdb.Card;

public class CardFragment extends Fragment {
    public static final String PARAM_CARD = "CARD";
    public static final String TAG = "FRAG_CARD";

    private Card card;
    private CardListener listener;

    public static CardFragment newInstance(Card card) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_CARD, card);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_card, container, false);
        card = (Card) getArguments().getSerializable(PARAM_CARD);
        if (this.listener != null) {
            this.listener.OnCardDisplayed(card);
        }
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        prepareViews(layout);
        return layout;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InputMethodManager methodManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (methodManager != null) {
            View currentFocus = getActivity().getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
                methodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.listener = (CardListener) activity;
        } catch (ClassCastException e) {
            Log.i(TAG, "container is not listening");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (this.listener != null) {
            this.listener.OnCardClosed();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    private void prepareViews(View layout) {
        ScrollView scrollerBar = (ScrollView) layout.findViewById(R.id.scroller_bar);
        LinearLayout colorBar = (LinearLayout) layout.findViewById(R.id.color_bar);

        prepareScrollerBar(scrollerBar);
        prepareColorBar(colorBar);
    }

    private void prepareScrollerBar(View scrollerBar) {
        TextView flavorText = (TextView) scrollerBar.findViewById(R.id.flavor_text);
        TextView cardText = (TextView) scrollerBar.findViewById(R.id.card_text);

        if (card.getFlavor() != null) {
            flavorText.setText(card.getFlavor());
        } else flavorText.setVisibility(View.GONE);


        if (card.getCardText() != null) {
            String text = card.getCardText();
            cardText.setText(buildTextSpan(cardText, text), TextView.BufferType.SPANNABLE);
        } else cardText.setVisibility(View.GONE);
    }

    private SpannableString buildTextSpan(TextView textView, String text) {
        SpannableString textSpan = new SpannableString(text);

        Pattern pattern = Pattern.compile("\\[([CAS])\\]");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String match = matcher.group(1);
            String effect = null;
            switch (match) {
                case "C":
                    effect = "CONT";
                    break;
                case "A":
                    effect = "AUTO";
                    break;
                case "S":
                    effect = "ACT";
                    break;
            }
            if (effect != null) {
                EffectDrawable drawable = new EffectDrawable(textView, effect);
                ImageSpan effectSpan = new ImageSpan(drawable,
                        DynamicDrawableSpan.ALIGN_BOTTOM);
                textSpan.setSpan(effectSpan, matcher.start(), matcher.end(), 0);
            }
        }

        pattern = Pattern.compile("\\[(Clock?|Counter)\\]", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(text);
        while (matcher.find()) {
            String match = matcher.group(1).substring(0, 3).toLowerCase();
            Drawable icon = null;
            switch (match) {
                case "clo":
                    icon = getResources().getDrawable(R.drawable.clock_text);
                    break;
                case "cou":
                    icon = getResources().getDrawable(R.drawable.counter_text);
                    break;
            }
            if (icon != null) {
                icon.setBounds(0, 0, textView.getLineHeight(), textView.getLineHeight());
                ImageSpan iconSpan = new ImageSpan(icon, DynamicDrawableSpan.ALIGN_BOTTOM);
                textSpan.setSpan(iconSpan, matcher.start(), matcher.end(), 0);
            }
        }

        pattern = Pattern.compile("ACCELERATE|ALARM|ASSIST|BACKUP( \\d+, Level \\d)?|" +
                "BODYGUARD|BOND(/\"[^\"]+\")?|BRAINSTORM|CHANGE|ENCORE|EXPERIENCE|MEMORY|" +
                "RECOLLECTION|S(HIFT|hift)(,? Level \\d)?|RESONATE");
        matcher = pattern.matcher(text);

        while (matcher.find()) {
            textSpan.setSpan(new StyleSpan(Typeface.BOLD), matcher.start(), matcher.end(), 0);
        }

        return textSpan;
    }

    private void prepareColorBar(View colorBar) {
        TextView titleText = (TextView) colorBar.findViewById(R.id.title);
        TextView titleJpText = (TextView) colorBar.findViewById(R.id.title_jp);
        LinearLayout keywordBar = (LinearLayout) colorBar.findViewById(R.id.kw_bar);
        RelativeLayout powerBar = (RelativeLayout) colorBar.findViewById(R.id.power_bar);

        switch (card.getColor().toLowerCase()) {
            case "red":
                colorBar.setBackgroundResource(R.drawable.red_color_bar);
                break;
            case "blue":
                colorBar.setBackgroundResource(R.drawable.blue_color_bar);
                break;
            case "green":
                colorBar.setBackgroundResource(R.drawable.green_color_bar);
                break;
            case "yellow":
                colorBar.setBackgroundResource(R.drawable.yellow_color_bar);
                break;
        }

        titleText.setText(card.getTitle());
        titleJpText.setText(card.getTitleJp());

        preparePowerBar(powerBar);
        prepareKeywordBar(keywordBar);
    }

    private void preparePowerBar(View powerBar) {
        TextView codeText = (TextView) powerBar.findViewById(R.id.code);
        TextView powerText = (TextView) powerBar.findViewById(R.id.power);
        ImageView typeIcon = (ImageView) powerBar.findViewById(R.id.type_icon);

        powerText.setVisibility(View.GONE);
        typeIcon.setVisibility(View.GONE);

        String cardType = card.getType().toLowerCase();
        switch (cardType.toLowerCase()) {
            case "character":
                powerText.setVisibility(View.VISIBLE);
                break;
            case "event":
                typeIcon.setVisibility(View.VISIBLE);
                typeIcon.setImageResource(R.drawable.event_tx);
                break;
            case "climax":
                typeIcon.setVisibility(View.VISIBLE);
                typeIcon.setImageResource(R.drawable.climax_tx);
                break;
        }

        codeText.setText(card.getCode());
        powerText.setText(String.valueOf(card.getPower()));

        powerBar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Uri uri = Uri.parse("https://www.google.com/search?tbm=isch&q=" + card.getCode());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//                intent.putExtra(SearchManager.QUERY, "images of "+ card.getCode());
                startActivity(intent);
                return true;
            }
        });
    }

    private void prepareKeywordBar(View keywordBar) {
        TextView kwOne = (TextView) keywordBar.findViewById(R.id.kw_one);
        TextView kwTwo = (TextView) keywordBar.findViewById(R.id.kw_two);

        keywordBar.setVisibility(View.GONE);
        kwOne.setVisibility(View.GONE);
        kwTwo.setVisibility(View.GONE);

        if (card.getKwOne() != null) {
            keywordBar.setVisibility(View.VISIBLE);
            kwOne.setVisibility(View.VISIBLE);
            kwOne.setText(card.getKwOne());
        }

        if (card.getKwTwo() != null) {
            keywordBar.setVisibility(View.VISIBLE);
            kwTwo.setVisibility(View.VISIBLE);
            kwTwo.setText(card.getKwTwo());
        }
    }

    public interface CardListener {
        public void OnCardDisplayed(Card card);
        public void OnCardClosed();
    }
}
