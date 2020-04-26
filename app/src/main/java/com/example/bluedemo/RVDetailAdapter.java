package com.example.bluedemo;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.search.SearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: ZhangMin
 * @date: 2020/4/17 10:37
 * @version: 1.0
 * @desc:
 */
public class RVDetailAdapter extends RecyclerView.Adapter<RVDetailAdapter.MyTVHolder> {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<DetailItem> mData = new ArrayList<>();
    OnItemClickListener onItemClickListener;

    interface OnItemClickListener {
        void onItemClick(DetailItem detailItem1, DetailItem detailItem2);
    }

    public RVDetailAdapter(Context context, BluetoothDevice mDevice) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    private void setDataList(List<DetailItem> datas) {
        mData.clear();
        mData.addAll(datas);
        notifyDataSetChanged();
    }

    public void setGattProfile(BleGattProfile profile) {
        List<DetailItem> items = new ArrayList<DetailItem>();
        List<BleGattService> services = profile.getServices();

        for (BleGattService service : services) {
            items.add(new DetailItem(DetailItem.TYPE_SERVICE, service.getUUID(), null));
            List<BleGattCharacter> characters = service.getCharacters();
            for (BleGattCharacter character : characters) {
                items.add(new DetailItem(DetailItem.TYPE_CHARACTER, character.getUuid(), service.getUUID()));
            }
        }
        setDataList(items);
    }

    @Override
    public RVDetailAdapter.MyTVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RVDetailAdapter.MyTVHolder(mLayoutInflater.inflate(R.layout.device_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RVDetailAdapter.MyTVHolder holder, final int pos) {
        final DetailItem result = mData.get(pos);
        if (result.type == DetailItem.TYPE_SERVICE) {
            holder.root.setBackgroundColor(mContext.getResources().getColor(R.color.device_detail_service));
            holder.uuid.getPaint().setFakeBoldText(true);
            holder.uuid.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f);
            holder.uuid.setText(String.format("Service: %s", result.uuid.toString().toUpperCase()));
        } else {
            holder.root.setBackgroundColor(mContext.getResources().getColor(R.color.device_detail_character));
            holder.uuid.getPaint().setFakeBoldText(false);
            holder.uuid.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.0f);
            holder.uuid.setText(String.format("Characteristic: %s", result.uuid.toString().toUpperCase()));
        }

        if (getItemCount() - 3 < pos) {
            return;
        }
        if (result.uuid.toString().toUpperCase().startsWith("0000FFF0") && result.type == DetailItem.TYPE_SERVICE) {
            final DetailItem detailItem1 = mData.get(pos + 1);
            final DetailItem detailItem2 = mData.get(pos + 2);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(detailItem1, detailItem2);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void setOnItemclickListener(OnItemClickListener onItemclickListener) {
        this.onItemClickListener = onItemclickListener;
    }

    class MyTVHolder extends RecyclerView.ViewHolder {
        private View root;
        private TextView uuid;

        MyTVHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            uuid = (TextView) itemView.findViewById(R.id.uuid);
        }
    }
}
