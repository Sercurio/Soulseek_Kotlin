package fr.sercurio.saoul_seek.ui.fragments.child;

import androidx.fragment.app.Fragment;

public class ChildTransferFragment extends Fragment {
    /*public static ChildTransferFragment newInstance() {
        ChildTransferFragment childTransferFragment = new ChildTransferFragment();

        Bundle args = new Bundle();
        args.putInt("TRANSFER", 0);
        childTransferFragment.setArguments(args);

        return childTransferFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //searchedFiles = new ArrayList<>();

        View view;
        view = inflater.inflate(R.layout.fragment_childtransfer, container, false);

        EmptyRecyclerView recyclerSearchView = view.findViewById(R.id.recyclerFiles);
        recyclerSearchView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // adapter = new SearchedFileAdapter(searchedFiles);
        //recyclerSearchView.setAdapter(adapter);

        SearchView searchView = view.findViewById(R.id.searchView);

        Button button = view.findViewById(R.id.dlButton);
/*        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          Toast.makeText(getContext(),"test", Toast.LENGTH_SHORT);
                                      }
        });*/
/*
        return view;
    }*/
}
