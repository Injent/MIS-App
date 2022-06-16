package com.injent.miscalls.ui.adapters;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.injent.miscalls.R;
import com.injent.miscalls.ui.settings.SpinnerAdapter;

import java.util.List;

public class FieldAdapter extends ListAdapter<ViewType, FieldAdapter.ViewHolder> {

    private OnEditTextListener listener;

    public FieldAdapter(OnEditTextListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<ViewType> diffCallback = new DiffUtil.ItemCallback<>() {

        @Override
        public boolean areItemsTheSame(@NonNull ViewType oldItem, @NonNull ViewType newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ViewType oldItem, @NonNull ViewType newItem) {
            return oldItem.getViewType() == newItem.getViewType();
        }
    };

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public void submitList(@Nullable List<ViewType> list) {
        super.submitList(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(viewType, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewType layout = getItem(position);
        switch (layout.getViewType()) {
            case ViewType.FIELD_EDIT: holder.setEditFieldData(layout);
            break;
            case ViewType.FIELD_ADDITIONAL_TEXT: holder.setDefaultAdditionalFieldData(layout);
            break;
            case ViewType.FIELD_ADDITIONAL_DOUBLE_DECIMAL: holder.setDoubleDecimalFieldData(layout);
            break;
            case ViewType.FIELD_ADDITIONAL_SPINNER: holder.setSpinnerFieldData(layout);
            break;
            case ViewType.FIELD_ADDITIONAL_CHECKBOX: holder.setCheckBoxFieldData(layout);
            break;
            case ViewType.FIELD_ADDITIONAL_DECIMAL: holder.setDecimalFieldData(layout);
            break;
            default: holder.setSpace();
        }
    }

    public interface OnEditTextListener {
        void onEdit(int index, String value);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final OnEditTextListener listener;

        public ViewHolder(@NonNull View view, OnEditTextListener listener) {
            super(view);
            this.listener = listener;
        }

        public void setSpace() {
            // Nothing
        }

        public void setEditFieldData(ViewType layout) {
            Field field = (Field) layout;
            TextView name = itemView.findViewById(R.id.fieldEditSubtitle);
            name.setText(field.getNameStringResId());
            EditText editText = itemView.findViewById(R.id.fieldEditText);
            editText.setHint(field.getHintStringResId());
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Nothing to do
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    listener.onEdit(field.getIndex(), editText.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // Nothing to do
                }
            });
        }

        public void setDefaultAdditionalFieldData(ViewType layout) {
            TextView textView = itemView.findViewById(R.id.fieldAddName);
            EditText editText = itemView.findViewById(R.id.fieldAddEditField);

            AdditionalField field = (AdditionalField) layout;
            textView.setText(field.getNameResId());
            ConstraintLayout container = itemView.findViewById(R.id.fieldAddContainer);
            container.setActivated(false);
            editText.setOnFocusChangeListener((view, focused) -> container.setActivated(focused));
            editText.setHint(field.getExtraResId());
            editText.setText(field.getData());
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Nothing to do
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    listener.onEdit(field.getIndex(), editText.getText().toString());
                    ImageView done = itemView.findViewById(R.id.fieldDone);
                    if (charSequence.length() != 0) {
                        done.setVisibility(View.VISIBLE);
                    } else {
                        done.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // Nothing to do
                }
            });
        }

        public void setDoubleDecimalFieldData(ViewType layout) {
            AdditionalField field = (AdditionalField) layout;
            TextView name = itemView.findViewById(R.id.fieldDoubleDecText);
            name.setText(field.getNameResId());
            EditText editText1 = itemView.findViewById(R.id.fieldDoubleDecEdit1);
            EditText editText2 = itemView.findViewById(R.id.fieldDoubleDecEdit2);

            final int maxTextLength = 3;

            InputFilter[] editFilters = editText1.getFilters();
            InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
            System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
            newFilters[editFilters.length] = new InputFilter.LengthFilter(maxTextLength);
            editText1.setFilters(newFilters);
            editText2.setFilters(newFilters);
            if (!field.getData().equals("")) {
                String[] format = field.getData().split("/");
                editText1.setText(format[0]);
                editText2.setText(format[1]);
            }

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Nothing to do
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String format = editText1.getText().toString() + "/" + editText2.getText().toString();
                    listener.onEdit(field.getIndex(), format);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // Nothing to do
                }
            };
            editText1.addTextChangedListener(textWatcher);
            editText2.addTextChangedListener(textWatcher);
        }

        public void setDecimalFieldData(ViewType layout) {
            AdditionalField field = (AdditionalField) layout;
            TextView name = itemView.findViewById(R.id.fieldDecName);
            name.setText(field.getNameResId());
            EditText editText = itemView.findViewById(R.id.fieldDecVal);
            if (!field.getData().equals(""))
                editText.setText(field.getData());

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Nothing to do
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    listener.onEdit(field.getIndex(), editText.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // Nothing to do
                }
            });
        }

        public void setSpinnerFieldData(ViewType layout) {
            AdditionalField field = (AdditionalField) layout;
            ImageView done = itemView.findViewById(R.id.fieldSpinnerDone);
            TextView name = itemView.findViewById(R.id.fieldSpinnerText);
            name.setText(field.getNameResId());
            Spinner spinner = itemView.findViewById(R.id.fieldSpinner);
            String[] items = itemView.getResources().getStringArray(field.getExtraResId());
            SpinnerAdapter adapter = new SpinnerAdapter(itemView.getContext(), items);
            spinner.setAdapter(adapter);
            for (int i = 0; i < items.length; i++) {
                if (items[i].equals(field.getData())) {
                    spinner.setSelection(i);
                }
            }
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    if (pos != 0) {
                        done.setVisibility(View.VISIBLE);
                        listener.onEdit(field.getIndex(), adapter.getItem(pos));
                    } else if (done.getVisibility() == View.VISIBLE){
                        done.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Nothing to do
                }
            });
        }

        public void setCheckBoxFieldData(ViewType layout) {
            AdditionalField field = (AdditionalField) layout;
            TextView name = itemView.findViewById(R.id.fieldCheckBoxText);
            CheckBox checkBox = itemView.findViewById(R.id.fieldCheckBox);
            name.setText(field.getNameResId());

            if (field.getIndex() == Field.PENSIONER) {
                checkBox.setChecked(Boolean.parseBoolean(field.getData()));
            }

            checkBox.setOnCheckedChangeListener((compoundButton, checked) -> {
                listener.onEdit(field.getIndex(), String.valueOf(checked));
            });
        }
    }
}
