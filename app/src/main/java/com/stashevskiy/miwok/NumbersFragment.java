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


public class NumbersFragment extends Fragment {


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

        // Создаем список чисел
        final ArrayList<Word> numbers = new ArrayList<>();
        numbers.add(new Word("one", "lutti", R.drawable.number_one, R.raw.number_one));
        numbers.add(new Word("two", "otiiko", R.drawable.number_two, R.raw.number_two));
        numbers.add(new Word("three", "tolockosu", R.drawable.number_three, R.raw.number_three));
        numbers.add(new Word("four", "oyyisa", R.drawable.number_four, R.raw.number_four));
        numbers.add(new Word("five", "massokka", R.drawable.number_five, R.raw.number_five));
        numbers.add(new Word("six", "temmokka", R.drawable.number_six, R.raw.number_six));
        numbers.add(new Word("seven", "kenekaku", R.drawable.number_seven, R.raw.number_seven));
        numbers.add(new Word("eight", "kawinta", R.drawable.number_eight, R.raw.number_eight));
        numbers.add(new Word("nine", "wo`e", R.drawable.number_nine, R.raw.number_nine));
        numbers.add(new Word("ten", "na`aacha", R.drawable.number_ten, R.raw.number_ten));


        // Создаем WordAdapter, источником данных которого является список объектов Word
        // адаптер знает, как создавать элементы списка для каждого элемента в списке
        WordAdapter adapter = new WordAdapter(getActivity(), numbers, R.color.category_numbers);

        // Находим ListView
        ListView list = rootView.findViewById(R.id.list);

        // Устанавливаем адаптер, который будет отображать данные в ListView для каждого объекта Word
        list.setAdapter(adapter);

        // Устанавливаем прослушиватель кликов для воспроизведения звука при нажатии на элемент списка
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Получаем объект Word в указанной позиции, на которую нажал пользователь
                Word number = numbers.get(position);

                // Выводим поле данных нажатого объекта в Log Messages
                Log.v(TAG, "Current number: " + number.toString());

                // Освобождаем медиаплеер, если он существует, потому что мы собираемся воспроизвести другой звуковой файл
                releaseMediaPlayer();

                // Запрашиваем аудио-фокус так, чтобы воспроизводить аудиофайл STREAM_MUSIC.
                // Приложение должно воспроизводить короткий аудиофайл, поэтому мы запросим аудио-фокус
                // с небольшим количеством времени с помощью AUDIOFOCUS_GAIN_TRANSIENT
                int requestResult = audioManager.requestAudioFocus(audioFocusChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                // Если аудио-фокус получен
                if(requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                    // Создаем и настраиваем MediaPlayer для аудиоресурса, связанного с текущим словом
                    player = MediaPlayer.create(getActivity(), number.getAudioResourceId());

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
