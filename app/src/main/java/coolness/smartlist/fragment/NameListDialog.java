package coolness.smartlist.fragment;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import coolness.smartlist.R;
import coolness.smartlist.ListManager;
import coolness.smartlist.model.CurrentList;

public class NameListDialog extends DialogFragment {

    EditText nameView;
    CheckBox databaseBox;
    Button addButton;
    Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_name_list, new ConstraintLayout(getActivity()), false);
        nameView = view.findViewById(R.id.nameView);
        nameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addButton.setEnabled(charSequence.toString().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListManager.addCurrentList(new CurrentList(nameView.getText().toString()), getActivity());
                NameListDialog.this.dismiss();
            }
        });
        cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NameListDialog.this.dismiss();
            }
        });


        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setContentView(view);
        //builder.setCanceledOnTouchOutside(false);
        return builder;
    }
}
