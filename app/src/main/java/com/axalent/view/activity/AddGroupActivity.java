package com.axalent.view.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.model.data.database.DBManager;
import com.axalent.model.data.model.Area;
import com.axalent.model.data.model.devices.CSRDevice;
import com.axalent.presenter.RxBus;
import com.axalent.presenter.events.MeshRequestEvent;
import com.axalent.presenter.events.MeshResponseEvent;
import com.axalent.util.AxalentUtils;
import com.axalent.util.DividerItemDecoration;
import com.axalent.adapter.DevicePickerAdapter;
import com.axalent.util.LogUtils;
import com.axalent.view.material.Constants;
import com.axalent.view.widget.LoadingDialog;
import com.csr.csrmesh2.GroupModelApi;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * File Name                   : AddGroupActivity
 * Author                      : franky
 * Version                     : 1.0.0
 * Date                        : 2016/9/19
 * Revision History            : 14:25
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd.
 * All rights reserved.
 */
public class AddGroupActivity extends BaseActivity {

    public static String KEY_AREA_ID = "id";

    private EditText mGroupName;
    private RecyclerView mDeviceView;
    private List<CSRDevice> csrDevices;
    private DevicePickerAdapter mAdapter;
    private Area area;
    private String tempAreaName = "";
    private Queue<Integer> mDevicesToAdd = new LinkedList<>();
    private Queue<Integer> mDevicesToDelete = new LinkedList<>();
    private List<CSRDevice> applyDevices = new ArrayList<>();
    private List<CSRDevice> removeDevices = new ArrayList<>();
    private LoadingDialog loadingDialog;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = DBManager.getDBManagerInstance(this.getApplicationContext());
        setContentView(R.layout.activity_add_group);
        initActionBar();
        initView();
    }

    private void initActionBar() {
        View content = findViewById(R.id.barGroupContent);
        RelativeLayout barGroup = (RelativeLayout) content.findViewById(R.id.barGroup);
        TextView addGroup = (TextView) content.findViewById(R.id.barGroupTitleTxt);
        TextView cancelGroup = (TextView) content.findViewById(R.id.barCancel);

        barGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add group
                confirmGroup();
            }
        });

        addGroup.setText(getString(R.string.add_group));

        cancelGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initView() {
        mGroupName = (EditText) findViewById(R.id.group_name);
        mDeviceView = (RecyclerView) findViewById(R.id.device_list);
        mDeviceView.setLayoutManager(new LinearLayoutManager(this));
        mDeviceView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // Get the device to be displayed/edited.
        int id = getIntent().getIntExtra(KEY_AREA_ID, Constants.INVALID_VALUE);
        LogUtils.i("areaId:" + id);
        area = dbManager.getArea(id);

        List<Integer> listIds = new ArrayList<>();
        if (area == null) {
            area = new Area();
            area.setAreaID(dbManager.getNewAreaId());
            area.setPlaceID(dbManager.getPlace(1).getId());
        } else {
            tempAreaName = area.getName();
            mGroupName.setText(tempAreaName);

            List<CSRDevice> devices = dbManager.getDevicesInArea(area.getId());

            for (int i = 0; i < devices.size(); i++) {
                listIds.add(devices.get(i).getId());
                LogUtils.i("selectId:" + devices.get(i).getName());
            }
        }

        csrDevices = dbManager.getAllDevicesList();
        Iterator it = csrDevices.iterator();
        while (it.hasNext()) {
            CSRDevice device = (CSRDevice) it.next();
            if (device.getNumGroups() <= 0) {
                it.remove();
            }
        }

        mAdapter = new DevicePickerAdapter(csrDevices, listIds, this);
        mDeviceView.setAdapter(mAdapter);

        loadingDialog = new LoadingDialog(this);

    }

    private void confirmGroup() {
        String groupName = mGroupName.getText().toString().trim();
        if (groupName.isEmpty()) {
            Toast.makeText(this, getString(R.string.group_name_not_empty), Toast.LENGTH_SHORT).show();
            return;
        } else {
            area.setName(groupName);
        }

        List<Integer> devicesToAdd = mAdapter.getDevicesToAdd();
        List<Integer> devicesToDelete = mAdapter.getDevicesToDelete();
        if (devicesToAdd != null) {
            mDevicesToAdd.addAll(devicesToAdd);
        }
        if (devicesToDelete != null) {
            mDevicesToDelete.addAll(devicesToDelete);
        }

        if (mDevicesToAdd.isEmpty() && mDevicesToDelete.isEmpty()) {
            if (!area.getName().equals(tempAreaName)) {
                dbManager.createOrUpdateArea(area);
                setResult(AxalentUtils.ADD_GROUP);
                RxBus.getDefaultInstance().post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.REFRESH_PAGE));
                Log.i("confirmGroup", "confirmGroup: no add and delete");
            }
            finish();
        }
        else {
            loadingDialog.show(R.string.applied);
            boolean inprogress = peekNextOperation();
            if (inprogress) {
                Area result = dbManager.createOrUpdateArea(area);
                if (result != null) {
                    CSRDevice device = null;
                    for (CSRDevice d : removeDevices) {
                        device = dbManager.createOrUpdateDevice(d);
                    }
                    for (CSRDevice d : applyDevices) {
                        device = dbManager.createOrUpdateDevice(d);
                    }
                    if (device != null) {
                        loadingDialog.close();
                        Toast.makeText(this, getString(R.string.area_applied), Toast.LENGTH_SHORT).show();
                        setResult(AxalentUtils.ADD_GROUP);
                        RxBus.getDefaultInstance().post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.REFRESH_PAGE));
                        finish();
                    } else {
                        loadingDialog.close();
                        Toast.makeText(this, getString(R.string.create_area_failure), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadingDialog.close();
                    Toast.makeText(this, getString(R.string.area_failure), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                loadingDialog.close();
                Toast.makeText(this, getString(R.string.area_failure), Toast.LENGTH_SHORT).show();
//                finish();
            }
        }
    }

    private boolean peekNextOperation() {
        boolean operation = false;
        if (!mDevicesToDelete.isEmpty()) {
            for (Integer i : mDevicesToDelete) {
                CSRDevice mDeviceToApply = dbManager.getDevice(i);
                if (mDeviceToApply == null) {
                    mDevicesToDelete.remove();
                }
                else {
                    int groupIndex = mDeviceToApply.getGroupsList().indexOf(area.getAreaID());
                    if (groupIndex == Constants.INVALID_VALUE) {
                        mDevicesToDelete.remove();
                        Toast.makeText(this,getString(R.string.maximum_areas), Toast.LENGTH_SHORT).show();
                        operation = false;
                    }
                    else {
                        mDeviceToApply.setGroup(groupIndex, 0);
                        GroupModelApi.setModelGroupId(mDeviceToApply.getDeviceID(), 0xFF, groupIndex, 0, 0);
                        operation = true;
                    }
                }
                removeDevices.add(mDeviceToApply);
            }

        }
        else if (!mDevicesToAdd.isEmpty()) {
            for (Integer i : mDevicesToAdd) {
                CSRDevice mDeviceToApply = dbManager.getDevice(i);
                if (mDeviceToApply == null) {
                    mDevicesToAdd.remove();
                }
                else {
                    int groupIndex = mDeviceToApply.getGroupsList().indexOf(0);
                    if (groupIndex == Constants.INVALID_VALUE) {
                        mDevicesToAdd.remove();
                        Toast.makeText(this,mDeviceToApply.getName() + getString(R.string.maximum_areas), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    else {
                        mDeviceToApply.setGroup(groupIndex, area.getAreaID());
                        GroupModelApi.setModelGroupId(mDeviceToApply.getDeviceID(), 0xFF, groupIndex, 0, area.getAreaID());
                        operation = true;
                    }
                }
                applyDevices.add(mDeviceToApply);
            }
        }
        return operation;
    }
}
