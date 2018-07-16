package tech.goda.studyck;

        import android.content.Context;
        import android.graphics.drawable.Drawable;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentTransaction;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.jsoup.Jsoup;
        import org.jsoup.nodes.Document;
        import org.jsoup.nodes.Element;

        import java.util.HashMap;
        import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChpassFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChpassFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChpassFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private EditText account, oldPass,  newPass, newPassConf, code;
    private Button button;
    Bundle bundle;

    private OnFragmentInteractionListener mListener;
    private ImageView imageView;

    public ChpassFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChpassFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChpassFragment newInstance(String param1, String param2) {
        ChpassFragment fragment = new ChpassFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chpass, container, false);
        bundle = this.getArguments();

        button = view.findViewById(R.id.button);
        newPass = view.findViewById(R.id.passwordNew);
        oldPass = view.findViewById(R.id.passwordOld);
        account = view.findViewById(R.id.account);
        code = view.findViewById(R.id.code);
        newPassConf = view.findViewById(R.id.passwordConf);
        imageView = view.findViewById(R.id.imageView);

        try{
            account.setText(bundle.getString("account"));
        } catch(NullPointerException e){
            e.printStackTrace();
        }



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "Hello Toast", Toast.LENGTH_SHORT).show();
                final Map<String, String> params = new HashMap<>();
                params.put("username", account.getText().toString());
                params.put("oldPassword", oldPass.getText().toString());
                params.put("newPassword1", newPass.getText().toString());
                params.put("newPassword2", newPassConf.getText().toString());
                params.put("f_magiccode", code.getText().toString());
                params.put("submitted", "變更"); // this field must have value to make system work

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String response = Network.httpsRequestPost(Network.CHANGE_PWD_SAVE, params); // Query this page to get the confirm code
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String str = "";
                                if(response == null)
                                    Toast.makeText(getContext(), "網路異常", Toast.LENGTH_SHORT).show();
                                else {
                                    Document doc = Jsoup.parse(response);
                                    if (!doc.select(".msg_no").isEmpty()) {
                                        Element error = doc.selectFirst(".msg_no").selectFirst("p");
                                        str = error.text();
                                    } else if (!doc.select(".msg_yes").isEmpty()) {
                                        Element success = doc.selectFirst(".msg_yes").selectFirst("p");
                                        str = success.text();
                                        // TODO: switch fragment  here
                                        /*Fragment fragment = new ChpassFragment();
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, fragment);
                                        ft.commit();*/
                                    } else {
                                        str = "驗證碼錯誤";
                                    }
                                    Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();

                                    Log.e("TAG_INSIDE", response);
                                }
                            }
                        });
                    }

                }).start();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Network.httpsRequestPost(Network.CHANGE_PWD_URI, new HashMap<String, String>()); // Query this page to get the confirm code
                final Drawable drawable = Network.getDrawable(Network.CHANGE_PWD_CODE);
                Log.e("TAG", String.valueOf(drawable==null));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("TAG_INSIDE", String.valueOf(drawable==null));
                        imageView.setImageDrawable(drawable);
                    }
                });
            }

        }).start();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
