package org.pytorch.demo;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.valdesekamdem.library.mdtoast.MDToast;

import org.pytorch.demo.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CrewSelectAdapter extends RecyclerView.Adapter<CrewSelectAdapter.ViewPagerViewHolder> {
    ViewGroup parent;
    ArrayList<Util.Crew> crewArrayList;
    SwipeRefreshLayout swipeRefreshLayout;
    @NonNull

    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_select_face_datagram, parent, false);
        this.parent = parent;
        crewArrayList = new Util().get_all_crews();
        if (crewArrayList == null){
            MDToast.makeText(parent.getContext(), "没有本地文件，请去下载", MDToast.LENGTH_SHORT, MDToast.TYPE_INFO).show();
        }
//        System.out.println("in create crew array list len is " + crewArrayList.size());

        return new ViewPagerViewHolder(view);
    }

    ListView listView;
    ListView listView1;

    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
        if(position == 0){
            swipeRefreshLayout = holder.itemView.findViewById(R.id.swiperFresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh_lv0();
                }
            });
            System.out.println("in onbindviewholder position is "+position);
            listView = holder.itemView.findViewById(R.id.list);
            updateListView0(crewArrayList);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                AlertDialog alertDialog;

                public void delete_by_name(String name, String filename){
                    new Util().delete_by_name(name, filename);
                    MDToast.makeText(parent.getContext(), "成功删除", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                }


                @Override
                @SuppressLint("StaticFieldLeak")
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    System.out.println("position "+position);
                    String name = ((HashMap<String, String>)(listView.getItemAtPosition(position))).get("name");
                    String filename = ((HashMap<String, String>)(listView.getItemAtPosition(position))).get("file");
                    System.out.println(name);
//                Environment.getDataDirectory()

                    final String[] choices = new String[]{"预览", "删除"};

                    alertDialog = new AlertDialog.Builder(parent.getContext())
                            .setTitle("选择操作")
                            .setIcon(R.drawable.ic_logo_pytorch)
                            .setItems(choices, new DialogInterface.OnClickListener() {//添加列表
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(i==0)//预览
                                    {
                                        //TODO call media here
                                        File file = new File(new Util().datagram_path , filename);
                                        System.out.println("in onclick datagram path is " + file.getAbsolutePath());
                                        Intent intent = new Intent(Intent.ACTION_VIEW);


                                        if (file.exists()){
                                            Uri contentUri = FileProvider.getUriForFile(parent.getContext(), "authority", file);
//                                            Uri uri = Uri.fromFile(file);
                                            parent.getContext().grantUriPermission(parent.getContext().getPackageName(), contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                            String type = Utils.getMIMEType(file);
                                            System.out.println("type is " + type);
                                            System.out.println("uri is " + contentUri.getPath());
                                            intent.setDataAndType(contentUri, type);
                                            parent.getContext().startActivity(intent);
                                            MDToast.makeText(parent.getContext(), "成功打开", Utils.dura_short, Utils.Type_success).show();
                                        }else{
                                            MDToast.makeText(parent.getContext(), "文件不存在，文件路径错误", Utils.dura_short, Utils.Type_error).show();
                                        }
                                    }
                                    else if (i == 1)// 删除
                                    {
                                        new AlertDialog.Builder(parent.getContext())
                                                .setTitle("删除 " + name)
                                                .setMessage("是否确定删除 "+filename+" 下的" + name)

                                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                                // The dialog is automatically dismissed when a dialog button is clicked.
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Continue with delete operation
                                                        delete_by_name(name, filename);
                                                    }
                                                })

                                                // A null listener allows the button to dismiss the dialog and take no further action.
                                                .setNegativeButton(android.R.string.no, null)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    }
//                                    Toast.makeText(parent.getContext(), "点的是：" + choices[i], Toast.LENGTH_SHORT).show();
                                }

                            })
                            .create();

                    alertDialog.show();

                }
            });
        }
        else {
            swipeRefreshLayout = holder.itemView.findViewById(R.id.swiperFresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh_lv1();
                }
            });
            System.out.println("in onbindviewholder position is " + position);
            listView1 = holder.itemView.findViewById(R.id.list);
            updateListView1(crewArrayList);

            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                AlertDialog alertDialog;

                public void delete_by_name(String name, String filename){
                    new Util().delete_by_name(name, filename);
                    MDToast.makeText(parent.getContext(), "成功删除", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                }
                public void upload_dialog(String filename){
//                    alertDialog.hide();
                    alertDialog.dismiss();
                    System.out.println("before create pd");
                    ProgressDialog dialog = new ProgressDialog(parent.getContext());
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setTitle("上传中");
                    System.out.println("before setting listener");
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            MDToast.makeText(parent.getContext(), "上传成功", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                        }
                    });
                    dialog.setMessage("正在上传，请稍等");
                    System.out.println("before show");
                    dialog.show();
//                    dialog.setMax(100);

                    Util util = new Util();
                    new AsyncTask<String, Integer, String>(){
                        @Override
                        protected String doInBackground(String... arg0){
                            String res = util.UploadVideoByName(arg0[0]);
                            return res;
                        }

                        protected void onPostExecute(String result){
                            if (result != null){
                                MDToast.makeText(parent.getContext(), "上传完成",MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                System.out.println(result);
                            }else{
                                MDToast.makeText(parent.getContext(), "上传失败，检查网络或服务器",MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                            }
                        }
                    }.execute(filename);

                    System.out.println("before while");
                    while(util.upload_progress < 0.99){
                        System.out.println("in while up is "+ util.upload_progress);
                        dialog.setProgress((int)(100 * util.upload_progress));
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
//                    Toast.makeText(parent.getContext(),"上传成功", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }

                @Override
                @SuppressLint("StaticFieldLeak")
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    System.out.println("position "+position);
                    String name = ((HashMap<String, String>)(listView1.getItemAtPosition(position))).get("name");
                    String filename = ((HashMap<String, String>)(listView1.getItemAtPosition(position))).get("file");
                    System.out.println(name);
//                Environment.getDataDirectory()

                    final String[] choices = new String[]{"预览", "上传", "删除"};

                    alertDialog = new AlertDialog.Builder(parent.getContext())
                            .setTitle("选择操作")
                            .setIcon(R.drawable.ic_logo_pytorch)
                            .setItems(choices, new DialogInterface.OnClickListener() {//添加列表
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(i==0)//预览
                                    {
                                        //TODO call media here
                                        File file = new File(new Util().datagram_path , filename);
                                        System.out.println("in onclick datagram path is " + file.getAbsolutePath());
                                        Intent intent = new Intent(Intent.ACTION_VIEW);


                                        if (file.exists()){
                                            Uri contentUri = FileProvider.getUriForFile(parent.getContext(), "authority", file);
//                                            Uri uri = Uri.fromFile(file);
                                            parent.getContext().grantUriPermission(parent.getContext().getPackageName(), contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                            String type = Utils.getMIMEType(file);
                                            System.out.println("type is " + type);
                                            System.out.println("uri is " + contentUri.getPath());
                                            intent.setDataAndType(contentUri, type);
                                            parent.getContext().startActivity(intent);
                                            MDToast.makeText(parent.getContext(), "成功打开", Utils.dura_short, Utils.Type_success).show();
                                        }else{
                                            MDToast.makeText(parent.getContext(), "文件不存在，文件路径错误", Utils.dura_short, Utils.Type_error).show();
                                        }
                                    }
                                    else if (i == 1)// 上传
                                    {
                                        MDToast.makeText(parent.getContext(), "服务器尚没有该功能", MDToast.LENGTH_SHORT,MDToast.TYPE_INFO).show();
//                                        upload_dialog(name);
                                    }
                                    else if (i == 2)// 删除
                                    {
                                        new AlertDialog.Builder(parent.getContext())
                                                .setTitle("删除 " + name)
                                                .setMessage("是否确定删除 "+filename+" 下的" + name)

                                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                                // The dialog is automatically dismissed when a dialog button is clicked.
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Continue with delete operation
                                                        delete_by_name(name, filename);
                                                        refresh_lv0();
                                                        refresh_lv1();
                                                    }
                                                })

                                                // A null listener allows the button to dismiss the dialog and take no further action.
                                                .setNegativeButton(android.R.string.no, null)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();

                                    }
//                                    Toast.makeText(parent.getContext(), "点的是：" + choices[i], Toast.LENGTH_SHORT).show();
                                }

                            })
                            .create();

                    alertDialog.show();

                }
            });
        }

    }

    public void refresh_lv0(){
        crewArrayList = new Util().get_all_crews();
        updateListView0(crewArrayList);
        MDToast.makeText(parent.getContext(),"刷新成功", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
        swipeRefreshLayout.setRefreshing(false);
    }
    public void refresh_lv1(){
        crewArrayList = new Util().get_all_crews();
        updateListView1(crewArrayList);
        MDToast.makeText(parent.getContext(),"刷新成功", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
        swipeRefreshLayout.setRefreshing(false);
    }



    public void updateListView1(ArrayList<Util.Crew> crewArrayList){
        if (crewArrayList == null)
            return;
        ArrayList<Map<String,String>> list = new ArrayList<>();
        for (int i = 0; i < crewArrayList.size(); i++){
            String filename = crewArrayList.get(i).getCrew_file().getName();
            System.out.println("in updatelistview1 get name " + filename);
            if(filename.startsWith("local_temp_datagram")){
                Map<String, String> map= new HashMap<>();
                map.put("name", crewArrayList.get(i).getCrew_id());
                map.put("file", crewArrayList.get(i).getCrew_file().getName());
//                map.put("site", jsonArrays.getString(i));
                list.add(map);
            }

        }


        SimpleAdapter adapter = new SimpleAdapter(parent.getContext(),
                list,
                R.layout.datagram_list_item,
                new String[]{"name", "file"},
                new int[]{R.id.text1, R.id.text2});

        listView1.setAdapter(adapter);
        listView1.bringToFront();
    }

    public void updateListView0(ArrayList<Util.Crew> crewArrayList){
        if (crewArrayList == null)
            return;
        ArrayList<Map<String,String>> list = new ArrayList<>();
        for (int i = 0; i < crewArrayList.size(); i++){
            String filename = crewArrayList.get(i).getCrew_file().getName();
            System.out.println("in updatelistview0 get name " + filename);

            Map<String, String> map= new HashMap<>();
            map.put("name", crewArrayList.get(i).getCrew_id());
            map.put("file", crewArrayList.get(i).getCrew_file().getName());
//                map.put("site", jsonArrays.getString(i));
            list.add(map);
            System.out.println("in updatelistview0 list size " + list.size());
        }


        SimpleAdapter adapter = new SimpleAdapter(parent.getContext(),
                list,
                R.layout.datagram_list_item,
                new String[]{"name", "file"},
                new int[]{R.id.text1, R.id.text2});

        listView.setAdapter(adapter);
        listView.bringToFront();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public CrewSelectAdapter() {
        super();
    }

    public class ViewPagerViewHolder extends RecyclerView.ViewHolder{
        public ViewPagerViewHolder(View itemView){
            super(itemView);
        }
    }

}
