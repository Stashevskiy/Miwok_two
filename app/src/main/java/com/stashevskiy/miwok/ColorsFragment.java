package com.stashevskiy.miwok;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class ColorsFragment extends Fragment {


    // Обрабатывает воспроизведение всех звуковых файлов
    private MediaPlayer player;

    // Переменная для журнала сообщений
    private final static String TAG = "myLogs";

    // Этот прослушиватель запускается, когда MediaPlayer завершил воспроизведение аудиофайла
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            // Теперь, когда звуковой файл закончил воспроизведение, освобождаем ресурсы медиаплеера
            releaseMediaPlayer();
        }
    };

    // Обрабатывает аудио-фокус при воспроизведении звукового файла
    private AudioManager audioManager;

    // Этот прослушиватель запускается всякий раз, когда изменяется аудио-фокус
    // (т. е. мы получаем или теряем аудио-фокус из-за другого приложения или устройства).
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    // AUDIOFOCUS_LOSS_TRANSIENT означает, что мы потеряли аудио-фокус на короткий промежуток времени
                    // например - входящий звонок
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                            // AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK означает, что приложению
                            // разрешено продолжать воспроизведение звука, но с меньшей громкостью
                            // например - уведомление
                            focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {

                        // Мы будем рассматривать оба случая одинаково, потому что приложение
                        // воспроизводит короткие звуковые файлы. Приостановим воспроизведение и
                        // сбросим плеер до начала. Таким образом, мы можем играть слово с
                        // самого начала, когда возобновляем воспроизведение.
                        player.pause();
                        player.seekTo(0);
                        // AUDIOFOCUS_LOSS означает, что мы потеряли аудио-фокус звука и
                        // прекратили воспроизведение
                        // например - зашли в другое музыкальное приложение
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        // освобождаем ресурсы
                        releaseMediaPlayer();
                        // AUDIOFOCUS_GAIN означает, что мы восстановили аудио-фокус
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN){
                        // возобновляем воспроизведение
                        player.start();
                    }
                }
            };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);

        // Создаем и настраиваем AudioManager для запроса аудио-фокуса
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        // Создаем список цветов
        final ArrayList<Word> colors = new ArrayList<>();
        colors.add(new Word("red", "weṭeṭṭi", R.drawable.color_red, R.raw.color_red));
        colors.add(new Word("green", "chokokki", R.drawable.color_green, R.raw.color_green));
        colors.add(new Word("brown", "ṭakaakki", R.drawable.color_brown, R.raw.color_brown));
        colors.add(new Word("gray", "ṭopoppi", R.drawable.color_gray, R.raw.color_gray));
        colors.add(new Word("black", "kululli", R.drawable.color_black, R.raw.color_black));
        colors.add(new Word("white", "kelelli", R.drawable.color_white, R.raw.color_white));
        colors.add(new Word("dusty yellow", "ṭopiisә", R.drawable.color_dusty_yellow, R.raw.color_dusty_yellow));
        colors.add(new Word("mustard yellow", "chiwiiṭә", R.drawable.color_mustard_yellow, R.raw.color_mustard_yellow));


        // Создаем WordAdapter, источником данных которого является список объектов Word
        // адаптер знает, как создавать элементы списка для каждого элемента в списке
        WordAdapter adapter = new WordAdapter(getActivity(), colors, R.color.category_colors);

        // Находим ListView
        ListView list = rootView.findViewById(R.id.list);

        // Устанавливаем адаптер, который будет отображать данные в ListView для каждого объекта Word
        list.setAdapter(adapter);

        // Устанавливаем прослушиватель кликов для воспроизведения звука при нажатии на элемент списка
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Получаем объект Word в указанной позиции, на которую нажал пользователь
                Word color = colors.get(position);

                // Выводим поле данных нажатого объекта в Log Messages
                Log.v(TAG, "Current color: " + color.toString());

                // Освобождаем медиаплеер, если он существует, потому что мы собираемся воспроизвести другой звуковой файл
                releaseMediaPlayer();

                // Запрашиваем аудиофокус так, чтобы воспроизводить аудиофайл STREAM_MUSIC.
                // Приложение должно воспроизводить короткий аудиофайл, поэтому мы запросим аудио-фокус
                // с небольшим количеством времени с помощью AUDIOFOCUS_GAIN_TRANSIENT
                int requestResult = audioManager.requestAudioFocus(audioFocusChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                // Теперь есть аудио-фокус
                if(requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                    // Создаем и настраиваем MediaPlayer для аудиоресурса, связанного с текущим словом
                    player = MediaPlayer.create(getActivity(), color.getAudioResourceId());

                    // Запуск аудиофайла
                    player.start();

                    // Настраиваем прослушиватель на медиаплеерах, чтобы мы могли остановить и
                    // освободить медиаплеер после завершения воспроизведения звука
                    player.setOnCompletionListener(onCompletionListener);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        // Когда fragment остановлен, освобождаем ресурсы медиаплеера,
        // потому что мы больше не будем воспроизводить звуки
        releaseMediaPlayer();
    }

    // Очищаем медиаплеер, освобождая его ресурсы
    private void releaseMediaPlayer() {

        // Если медиаплеер не равен нулю, он может воспроизводить звук в данный момент
        if (player != null) {

            // Независимо от текущего состояния медиаплеера, освобождаем его ресурсы, потому что он больше не нужен
            player.release();

            // Устанавливаем медиаплеер равным нулю, для того чтобы указать,
            // что медиаплеер не настроен на воспроизведение аудиофайла
            player = null;

            // Независимо от того, был ли нами предоставлен аудио-фокус, освобождаемся от него.
            // Это также отменяет регистрацию AudioFocusChangeListener, поэтому мы не получаем больше обратных вызовов
            audioManager.abandonAudioFocus(audioFocusChangeListener);
        }
    }

}
