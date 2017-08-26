package com.antergy.whatsinmyfreezer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * The "home" fragment of the application. The user always returns to this fragment when starting
 * the application or pressing the toolbar back button.
 * Created by Johan on 2016-08-06.
 */
public class MainFragment extends Fragment {
    private Button mAddButton;
    private Button mContentButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mAddButton = (Button) view.findViewById(R.id.main_fragment_add_button);
        mContentButton = (Button) view.findViewById(R.id.main_fragment_content_button);

        setAddButton();
        setupContentButton();

        return view;
    }

    /**
     * Sets the add button. Takes the user to the AddActivity.
     */
    private void setAddButton() {
        mAddButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Sets the content button. Takes the user to the ContentListFragment that lists all food
     * items.
     */
    private void setupContentButton() {
        mContentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String category = getString(R.string.drawer_menu_all);
                Fragment fragment = ContentListFragment.newInstance(category);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
                getActivity().setTitle(category);
                ((MainActivity)getActivity()).setAllCategoryAsChecked();
            }
        });
    }
}
