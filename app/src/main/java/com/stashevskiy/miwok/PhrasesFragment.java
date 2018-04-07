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


public class PhrasesFragment extends Fragment {

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

        // Создаем список фраз
        final ArrayList<Word> phrases = new ArrayList<>();
        phrases.add(new Word("Where are you going?", "minto wuksus", R.raw.phrase_where_are_you_going));
        phrases.add(new Word("What is your name?", "tinnә oyaase'nә", R.raw.phrase_what_is_your_name));
        phrases.add(new Word("My name is...", "oyaaset...", R.raw.phrase_my_name_is));
        phrases.add(new Word("How are you feeling?", "michәksәs?", R.raw.phrase_how_are_you_feeling));
        phrases.add(new Word("I’m feeling good.", "kuchi achit", R.raw.phrase_im_feeling_good));
        phrases.add(new Word("Are you coming?", "әәnәs'aa?", R.raw.phrase_are_you_coming));
        phrases.add(new Word("Yes, I’m coming.", "hәә’ әәnәm", R.raw.phrase_yes_im_coming));
        phrases.add(new Word("I’m coming.", "әәnәm", R.raw.phrase_im_coming));
        phrases.add(new Word("Let’s go.", "yoowutis", R.raw.phrase_lets_go));
        phrases.add(new Word("Come here.", "әnni'nem", R.raw.phrase_come_here));


        // Создаем WordAdapter, источником данных которого является список объектов Word
        // адаптер знает, как создавать элементы списка для каждого элемента в списке
        WordAdapter adapter = new WordAdapter(getActivity(), phrases, R.color.category_phrases);

        // Находим ListView
        ListView list = rootView.findViewById(R.id.list);

        // Устанавливаем адаптер, который будет отображать данные в ListView для каждого объекта Word
        list.setAdapter(adapter);

        // Устанавливаем прослушиватель кликов для воспроизведения звука при нажатии на элемент списка
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Получаем объект Word в указанной позиции, на которую нажал пользователь
                Word phrase = phrases.get(position);

                // Выводим поле данных нажатого объекта в Log Messages
                Log.v(TAG, "Current phrase: " + phrase.toString());

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
                    player = MediaPlayer.create(getActivity(), phrase.getAudioResourceId());

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
        // Когда fragment остановлено, освобождаем ресурсы медиаплеера,
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
