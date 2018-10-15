package com.mygroupcx;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;

public class MemberActivity extends AppCompatActivity {
    @BindView(R.id.action_btn_0)
    MaterialButton actionBtn0;
    @BindView(R.id.action_btn_1)
    MaterialButton actionBtn1;
    @BindView(R.id.serial_number_tv)
    TextView serialNumberTv;
    @BindView(R.id.nickname_tv)
    TextView nicknameTv;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.gender_tv)
    TextView genderTv;
    @BindView(R.id.age_tv)
    TextView ageTv;
    @BindView(R.id.type_tv)
    TextView typeTv;
    @BindView(R.id.level_tv)
    TextView levelTv;
    @BindView(R.id.liveness_tv)
    TextView livenessTv;

    private Box<MemberEntity> memberEntityBox;

    public static final int MODE_CREATE = 0x00;
    public static final int MODE_EDIT = 0x01;
    private int mode;
    private MemberEntity memberEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        ButterKnife.bind(this);
        memberEntityBox = DBHelper.INSTANCE.memberEntityBox;
        mode = getIntent().getIntExtra("mode", MODE_CREATE);

        initView();
    }

    private void initView() {
        switch (mode) {
            case MODE_CREATE:
                memberEntity = new MemberEntity();
                actionBtn0.setText("添加");
                break;
            case MODE_EDIT:
                memberEntity = (MemberEntity) getIntent().getSerializableExtra("entity");
                actionBtn0.setText("修改");

                actionBtn1.setText("删除");
                actionBtn1.setVisibility(View.VISIBLE);

                serialNumberTv.setText(String.valueOf(memberEntity.serialNumber));
                nicknameTv.setText(String.valueOf(memberEntity.nickname));
                nameTv.setText(String.valueOf(memberEntity.name));
                genderTv.setText(memberEntity.getGenderContent());
                ageTv.setText(memberEntity.getAgeContent());
                typeTv.setText(memberEntity.getTypeContent());
                levelTv.setText(memberEntity.getLevelContent());
                livenessTv.setText(memberEntity.getLivenessContent());
                break;
        }
    }

    @OnClick({R.id.action_btn_0, R.id.action_btn_1, R.id.serial_number_tv, R.id.nickname_tv, R.id.name_tv, R.id.gender_tv, R.id.age_tv, R.id.type_tv, R.id.level_tv, R.id.liveness_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.action_btn_0: {
                memberEntity.serialNumber = memberEntityBox.count() + 1;
                memberEntityBox.put(memberEntity);

                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setMessage(mode == MODE_CREATE ? "人员添加成功" : "人员修改成功")
                        .setPositiveButton("返回", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .show();
                break;
            }
            case R.id.action_btn_1: {
                memberEntityBox.remove(memberEntity);
                List<MemberEntity> all = memberEntityBox.getAll();
                for (int i = 0; i < all.size(); i++) {
                    MemberEntity memberEntity = all.get(i);
                    memberEntity.serialNumber = (i + 1);

                    memberEntityBox.put(memberEntity);
                }

                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setMessage("人员已删除")
                        .setPositiveButton("返回", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .show();
            }
            break;
            case R.id.serial_number_tv:
                break;
            case R.id.nickname_tv:
                input(new InputDialog.ActionListener() {
                    @Override
                    public void onConfirm(String content, Dialog dialog) {
                        nicknameTv.setText(content);
                        memberEntity.nickname = content;
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.name_tv:
                input(new InputDialog.ActionListener() {
                    @Override
                    public void onConfirm(String content, Dialog dialog) {
                        nameTv.setText(content);
                        memberEntity.name = content;
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.gender_tv: {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setSingleChoiceItems(new String[]{"小哥哥", "小姐姐"}, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        genderTv.setText("♂");
                                        break;
                                    case 1:
                                        genderTv.setText("♀");
                                        break;
                                }

                                memberEntity.gender = which;

                                dialog.dismiss();
                            }
                        })
                        .show();

                break;
            }
            case R.id.age_tv: {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setSingleChoiceItems(new String[]{"小鲜肉", "油腻中年", "老腊肉"}, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        ageTv.setText("小鲜肉");
                                        break;
                                    case 1:
                                        ageTv.setText("油腻中年");
                                        break;
                                    case 2:
                                        ageTv.setText("老腊肉");
                                        break;
                                }

                                memberEntity.age = which;

                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            }
            case R.id.type_tv: {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setSingleChoiceItems(new String[]{"普通成员", "核心成员", "管理", "群主"}, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        typeTv.setText("普通成员");
                                        break;
                                    case 1:
                                        typeTv.setText("核心成员");
                                        break;
                                    case 2:
                                        typeTv.setText("管理");
                                        break;
                                    case 3:
                                        typeTv.setText("群主");
                                        break;
                                }

                                memberEntity.type = which;

                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            }
            case R.id.level_tv: {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setSingleChoiceItems(new String[]{"萌新", "凡人", "神仙"}, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        levelTv.setText("萌新");
                                        break;
                                    case 1:
                                        levelTv.setText("凡人");
                                        break;
                                    case 2:
                                        levelTv.setText("神仙");
                                        break;
                                }

                                memberEntity.level = which;

                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            }
            case R.id.liveness_tv: {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setSingleChoiceItems(new String[]{"低", "中", "高", "敲级"}, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        livenessTv.setText("低");
                                        break;
                                    case 1:
                                        livenessTv.setText("中");
                                        break;
                                    case 2:
                                        livenessTv.setText("高");
                                        break;
                                    case 3:
                                        livenessTv.setText("敲级");
                                        break;
                                }

                                memberEntity.liveness = which;

                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            }
        }
    }

    private void input(InputDialog.ActionListener listener) {
        InputDialog inputDialog = new InputDialog(this);
        inputDialog.setListener(listener);
        inputDialog.show();
    }
}
