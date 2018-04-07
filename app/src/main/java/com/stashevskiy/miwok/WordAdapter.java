package com.stashevskiy.miwok;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class WordAdapter extends ArrayAdapter<Word> {

    // Идентификатор ресурса для цвета фона
    private int colorBackground;


    // Конструктор адаптера
    public WordAdapter(Context context, ArrayList<Word> words, int colorBackground) {
        super(context, 0, words);
        this.colorBackground = colorBackground;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Проверяем, используется ли существующее View повторно, иначе "раздуваем" View
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Получаем объект Word
        Word word = getItem(position);

        // Находим TextView в макете list_item.xml с идентификатором miwok_text_view
        TextView miwokTextView = listItemView.findViewById(R.id.miwok_text_view);

        // Получаем перевод Miwok из объекта Word и устанавливаем этот текст
        miwokTextView.setText(word.getMiwokTranslation());

        // Находим TextView в макете list_item.xml с идентификатором default_text_view
        TextView defaultTextView = listItemView.findViewById(R.id.default_text_view);

        // Получаем перевод по умолчанию из объекта Word и устанавливаем этот текст
        defaultTextView.setText(word.getDefaultTranslation());

        // Находим ImageView в макете list_item.xml с идентификатором image
        ImageView image = listItemView.findViewById(R.id.image);

        // Проверяем, предоставлено ли изображение для этого слова или нет
        if(word.hasImage()) {
            // Если изображение доступно, отображаем предоставленное изображение
            image.setImageResource(word.getImageResourceId());
            // Делаем ImageView видимым
            image.setVisibility(View.VISIBLE);
        } else {
            // В противном случае скрываем ImageView (устанавливаем видимость GONE)
            image.setVisibility(View.GONE);
        }

        // Находим LinearLayout
        LinearLayout linear = listItemView.findViewById(R.id.linear);

        // Устанавливаем фон в зависимости от категории слов (цвет передается в конструкторе адаптера)
        linear.setBackgroundResource(colorBackground);


        // Возвращаем весь макет элемента списка
        return listItemView;
    }
}
