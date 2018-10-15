package com.mygroupcx;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.query.QueryFilter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.mygroupcx.MemberActivity.MODE_EDIT;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private Box<MemberEntity> memberEntityBox;

    private Adapter adapter;
    private File dbFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        memberEntityBox = DBHelper.INSTANCE.memberEntityBox;
        dbFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "db");
        initView();
    }

    private void initView() {
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new Adapter(R.layout.item_member);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(MainActivity.this, MemberActivity.class);
                intent.putExtra("mode", MODE_EDIT);
                intent.putExtra("entity", ((Adapter) adapter).getItem(position));
                startActivity(intent);
            }
        });
        rv.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        refresh();
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.bg_config_black)
                .setTitle("配置")
                .setItems(new String[]{"全部", "查询", "添加", "谁走了？", "清空", "统计", "导出", "导入"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            //全部
                            case 0:
                                refresh();
                                break;
                            //查询    
                            case 1:
                                InputDialog inputDialog = new InputDialog(MainActivity.this);
                                inputDialog.setTitle("输入昵称");
                                inputDialog.setListener(new InputDialog.ActionListener() {
                                    @Override
                                    public void onConfirm(String content, Dialog dialog) {
                                        List<MemberEntity> matchList = memberEntityBox.find(MemberEntity_.nickname, content);

                                        if (ObjectUtils.isNotEmpty(matchList)) {
                                            adapter.setNewData(matchList);
                                        } else {
                                            ToastUtils.showLong("没有此成员");
                                        }

                                        dialog.dismiss();
                                    }
                                });

                                inputDialog.show();
                                break;
                            //添加
                            case 2:
                                startActivity(new Intent(MainActivity.this, MemberActivity.class));
                                break;
                            //谁走了？    
                            case 3:
                                List<MemberEntity> markList = memberEntityBox.query().filter(new QueryFilter<MemberEntity>() {
                                    @Override
                                    public boolean keep(MemberEntity entity) {
                                        return entity.serialNumber % 5 == 0;
                                    }
                                }).build().find();

                                adapter.setNewData(markList);
                                break;
                            //清空
                            case 4:
                                memberEntityBox.removeAll();
                                adapter.setNewData(null);
                                break;
                            //统计
                            case 5:

                                break;
                            //导出
                            case 6:
                                Observable.just(0)
                                        .observeOn(Schedulers.io())
                                        .map(new Function<Integer, Boolean>() {
                                            @Override
                                            public Boolean apply(Integer integer) throws Exception {
                                                try {
                                                    StringBuffer dbBuffer = new StringBuffer();

                                                    List<MemberEntity> all = memberEntityBox.getAll();
                                                    for (MemberEntity memberEntity : all) {
                                                        String jsonString = JSON.toJSONString(memberEntity);
                                                        dbBuffer.append(jsonString).append("EntitySplit");
                                                    }

                                                    FileIOUtils.writeFileFromString(dbFile, dbBuffer.toString(), false);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    return false;
                                                }

                                                return true;
                                            }
                                        })
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<Boolean>() {
                                            @Override
                                            public void accept(Boolean aBoolean) throws Exception {
                                                if (!aBoolean) {
                                                    ToastUtils.showLong("数据导出失败");
                                                    return;
                                                }

                                                ToastUtils.showLong("数据导出成功：" + dbFile.getAbsolutePath());
                                            }
                                        });
                                break;
                            //导入
                            case 7:
                                Observable.just(0)
                                        .observeOn(Schedulers.io())
                                        .map(new Function<Integer, Boolean>() {
                                            @Override
                                            public Boolean apply(Integer integer) throws Exception {
                                                try {
                                                    memberEntityBox.removeAll();

                                                    String jsonString = FileIOUtils.readFile2String(dbFile);

                                                    for (String json : jsonString.split("EntitySplit")) {
                                                        MemberEntity memberEntity = JSON.parseObject(json, MemberEntity.class);
                                                        memberEntityBox.put(memberEntity);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    return false;
                                                }

                                                return true;
                                            }
                                        })
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<Boolean>() {
                                            @Override
                                            public void accept(Boolean aBoolean) throws Exception {
                                                if (!aBoolean) {
                                                    ToastUtils.showLong("数据导入失败");
                                                    return;
                                                }

                                                ToastUtils.showLong("数据导入成功");
                                                refresh();
                                            }
                                        });
                                break;
                        }
                    }
                }).show();
    }

    private void refresh() {
        List<MemberEntity> all = memberEntityBox.getAll();
        adapter.setNewData(all);
    }

    class Adapter extends BaseQuickAdapter<MemberEntity, BaseViewHolder> {

        public Adapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder helper, MemberEntity item) {
            TextView serialNumberTextView = (TextView) helper.getView(R.id.item_serial_number_tv);
            serialNumberTextView.setText(String.valueOf(item.serialNumber));

            if (item.serialNumber % 5 == 0) {
                serialNumberTextView.setText(String.format("<%d>", item.serialNumber));
                serialNumberTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            } else {
                serialNumberTextView.setTextColor(getResources().getColor(android.R.color.black));
            }

            helper.setText(R.id.item_nickname_tv, String.valueOf(item.nickname));
            helper.setText(R.id.item_type_tv, item.getTypeContent());
            helper.setText(R.id.item_level_tv, item.getLevelContent());
            helper.setText(R.id.item_age_tv, item.getAgeContent());
            helper.setText(R.id.item_gender_tv, item.getGenderContent());
        }
    }
}
