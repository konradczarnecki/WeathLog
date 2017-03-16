package k.weathlog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;


public class SaveEntryFragment extends Fragment {

    private SaveEntryListener callbackListener;
    private DBHandler handler;

    public SaveEntryFragment() {}


    public static SaveEntryFragment newInstance() {
        SaveEntryFragment fragment = new SaveEntryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new DBHandler(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View saveEntryView = inflater.inflate(R.layout.fragment_save_entry, container, false);

        final EditText comment = (EditText) saveEntryView.findViewById(R.id.comment);
        ImageView saveCommentButton = (ImageView) saveEntryView.findViewById(R.id.save_comment_button);

        saveCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handler.addEntry(callbackListener.getCurrentWeather(), comment.getText().toString());
                callbackListener.onEntrySaved();
            }
        });


        return  saveEntryView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SaveEntryListener) {
            callbackListener = (SaveEntryListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SaveEntryListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbackListener = null;
    }


    public interface SaveEntryListener {

        void onEntrySaved();
        Weather getCurrentWeather();
    }
}
