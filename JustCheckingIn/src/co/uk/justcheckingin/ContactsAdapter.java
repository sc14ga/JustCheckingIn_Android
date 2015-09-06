
package co.uk.justcheckingin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Specifies how a Contact will be shown as an item of a ListView.
 * 
 * @author Georgios Aikaterinakis
 * @see CreateContactListActivity
 * @see EditContactListActivity
 * @see EmergencyContactListActivity
 */
class ContactsAdapter extends BaseAdapter {
    LayoutInflater mInflater;

    List<String> namesList = new ArrayList<String>();
    List<String> numbersList = new ArrayList<String>();
    List<Boolean> boxes = new ArrayList<Boolean>();

    @Override
    public int getCount() {
        return namesList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = mInflater.inflate(R.layout.listview_contacts, null);
        }

        TextView name = (TextView) vi.findViewById(R.id.name);
        TextView number = (TextView) vi.findViewById(R.id.number);
        CheckBox box = (CheckBox) vi.findViewById(R.id.checkBox);
        name.setText(namesList.get(position));
        number.setText(numbersList.get(position));
        box.setEnabled(true);
        box.setOnCheckedChangeListener(myCheckedChangeListener);
        box.setTag(position);
        box.setChecked(boxes.get(position));

        return vi;
    }

    OnCheckedChangeListener myCheckedChangeListener = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            boxes.set(((Integer) buttonView.getTag()), isChecked);
        }
    };
}
