package com.example.newproject.client.ui.mainActivitys.results;

import android.os.Looper;
import android.support.v4.app.Fragment;

import com.example.newproject.client.ui.mainActivitys.MainActivity;

public class BaseFragment extends Fragment {


    public void speakWithLooper(String str){
        Looper.prepare();
        ((MainActivity)getActivity()).speak(str);
        Looper.loop();
    }

    public void speak(String str) {
        ((MainActivity)getActivity()).speak(str);
    }

}
