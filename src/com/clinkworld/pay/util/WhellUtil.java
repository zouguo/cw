package com.clinkworld.pay.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.clinkworld.pay.R;
import com.clinkworld.pay.whell.CityAdapter;
import com.clinkworld.pay.whell.OnWheelChangedListener;
import com.clinkworld.pay.whell.OnWheelScrollListener;
import com.clinkworld.pay.whell.WheelView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * whell滑动选择器
 */
public class WhellUtil {
    private static WhellUtil instance = new WhellUtil();

    private String lev = "";  // 所有
    private int year;
    private int mouth;
    private int day;
    CityAdapter cityAdapter1;
    CityAdapter cityAdapter2;
    CityAdapter cityAdapter3;
    CityAdapter cityAdapter4;

    private String time = "";

    private WhellUtil() {

    }

    public static WhellUtil getInstance(Context context) {
        if (instance == null) {
            instance = new WhellUtil();
        }
        return instance;
    }

    /**
     * 时间选择器
     */
    public void showTimeWhell(final Activity activity, final TextView view, String ti,
                              WindowManager OPwindow, final Handler handler, final int type) {
        year = Integer.parseInt(ti.split(",")[0]);
        mouth = Integer.parseInt(ti.split(",")[1]);
        day = Integer.parseInt(ti.split(",")[2]);
        final String[] seconds = getSecondsTimeWhell();
        final String[] minutes = getMinuteTimeWhell();
        final String[] hours = {"01时", "02时", "03时", "04时", "05时", "06时", "07时", "08时", "09时", "10时", "11时",
                "12时", "13时", "14时", "15时", "16时", "17时", "18时", "19时", "20时", "21时", "22时", "23时", "00时"};

        String str = view.getText().toString();
        if ("请选择".equals(str)) {
            str = "";
        }
        int hour = 8;
        int min = 0;
        int second = 0;
        if (!TextUtils.isEmpty(str)) {
            try {
                day = Integer.parseInt(str.split(" ")[0].split("-")[1].split("-")[1]);
                hour = Integer.parseInt(str.split(" ")[1].split(":")[0]);
                min = Integer.parseInt(str.split(" ")[1].split(":")[1].split(":")[0]);
                second = Integer.parseInt(str.split(" ")[1].split(":")[1].split(":")[1]);
            } catch (Exception e) {

            }
        }

        if (OPwindow == null) {
            OPwindow = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.format = PixelFormat.TRANSLUCENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        final View Wview = LayoutInflater.from(activity).inflate(R.layout.select_time_dialog, null);

        final WheelView cities1 = (WheelView) Wview.findViewById(R.id.whell_1);
        cities1.setWheelBackground(R.drawable.wheel_bg_holo);
        cities1.setWheelForeground(R.drawable.wheel_val_holo);
        cityAdapter1 = new CityAdapter(activity, setTimeWhell(), day - 1);
        cities1.setViewAdapter(cityAdapter1);
        cities1.setCurrentItem(day - 1);

        cities1.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) cityAdapter1.getItemText(wheel.getCurrentItem());
                setTextviewColor(currentText, cityAdapter1);
            }
        });

        cities1.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) cityAdapter1.getItemText(wheel.getCurrentItem());
                setTextviewColor(currentText, cityAdapter1);
            }
        });

        final WheelView cities2 = (WheelView) Wview.findViewById(R.id.whell_2);
        cities2.setWheelBackground(R.drawable.wheel_bg_holo);
        cities2.setWheelForeground(R.drawable.wheel_val_holo);
        cityAdapter2 = new CityAdapter(activity, hours, hour - 1);
        cities2.setViewAdapter(cityAdapter2);
        cities2.setCurrentItem(hour - 1);

        cities2.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) cityAdapter2.getItemText(wheel.getCurrentItem());
                setTextviewColor(currentText, cityAdapter2);
            }
        });

        cities2.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) cityAdapter2.getItemText(wheel.getCurrentItem());
                setTextviewColor(currentText, cityAdapter2);
            }
        });

        final WheelView cities3 = (WheelView) Wview.findViewById(R.id.whell_3);
        cities3.setWheelBackground(R.drawable.wheel_bg_holo);
        cities3.setWheelForeground(R.drawable.wheel_val_holo);
        cityAdapter3 = new CityAdapter(activity, minutes, min);
        cities3.setViewAdapter(cityAdapter3);
        cities3.setCurrentItem(min);

        cities3.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) cityAdapter3.getItemText(wheel.getCurrentItem());
                setTextviewColor(currentText, cityAdapter3);
            }
        });

        cities3.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) cityAdapter3.getItemText(wheel.getCurrentItem());
                setTextviewColor(currentText, cityAdapter3);
            }
        });

        final WheelView cities4 = (WheelView) Wview.findViewById(R.id.whell_4);
        cities4.setWheelBackground(R.drawable.wheel_bg_holo);
        cities4.setWheelForeground(R.drawable.wheel_val_holo);
        cityAdapter4 = new CityAdapter(activity, seconds, second);
        cities4.setViewAdapter(cityAdapter4);
        cities4.setCurrentItem(second);

        cities4.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) cityAdapter4.getItemText(wheel.getCurrentItem());
                setTextviewColor(currentText, cityAdapter4);
            }
        });

        cities4.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) cityAdapter4.getItemText(wheel.getCurrentItem());
                setTextviewColor(currentText, cityAdapter4);
            }
        });

        final TextView timeText = (TextView) Wview.findViewById(R.id.show_time);
        if (mouth >= 10) {
            timeText.setText(year + "年" + mouth + "月");
        } else {
            timeText.setText(year + "年0" + mouth + "月");
        }
        Wview.findViewById(R.id.left_arror).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mouth > 1) {
                    mouth = mouth - 1;
                } else {
                    year = year - 1;
                    mouth = 12;
                }
                if (mouth < 10) {
                    timeText.setText(year + "年0" + mouth + "月");
                } else {
                    timeText.setText(year + "年" + mouth + "月");
                }
                cities1.setViewAdapter(null);
                int a = cities1.getCurrentItem();
                int d = cities1.getCurrentItem();
                if (setTimeWhell().length < d + 1) {
                    d = setTimeWhell().length - 1;
                }
                cities1.setViewAdapter(new CityAdapter(activity, setTimeWhell(), d));
                if (setTimeWhell().length < a + 1) {
                    cities1.setCurrentItem(setTimeWhell().length - 1);
                }
            }
        });
        Wview.findViewById(R.id.right_arror).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mouth < 12) {
                    mouth = mouth + 1;
                } else {
                    year = year + 1;
                    mouth = 1;
                }
                if (mouth < 10) {
                    timeText.setText(year + "年0" + mouth + "月");
                } else {
                    timeText.setText(year + "年" + mouth + "月");
                }
                cities1.setViewAdapter(null);
                cities1.setViewAdapter(new CityAdapter(activity, setTimeWhell(), cities1.getCurrentItem()));
                int a = cities1.getCurrentItem();
                if (setTimeWhell().length < a + 1) {
                    cities1.setCurrentItem(setTimeWhell().length - 1);
                }
            }
        });

        final WindowManager OP = OPwindow;
        Wview.findViewById(R.id.sub).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = cities1.getCurrentItem();
                int b = cities2.getCurrentItem();
                int c = cities3.getCurrentItem();
                int d = cities4.getCurrentItem();
                if (mouth < 10) {
                    view.setText(year + "-" + "0" + mouth + "-" + setTimeWhell()[a].split("日")[0] + " " + hours[b].split("时")[0] + ":" + minutes[c].split("分")[0] + ":" + seconds[d].split("秒")[0]);
                } else {
                    view.setText(year + "-" + mouth + "-" + setTimeWhell()[a].split("日")[0] + " " + hours[b].split("时")[0] + ":" + minutes[c].split("分")[0] + ":" + seconds[d].split("秒")[0]);
                }

                Message msg = new Message();
                msg.what = 5;
                msg.obj = year + "," + mouth + "," + setTimeWhell()[a].split("日")[0];
                msg.arg1 = type;
                handler.sendMessage(msg);
                OP.removeView(Wview);
            }
        });
        Wview.findViewById(R.id.lin).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = 5;
                msg.obj = "";
                msg.arg1 = type;
                handler.sendMessage(msg);
                OP.removeView(Wview);
            }
        });
        Wview.findViewById(R.id.lin2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				OP.removeView(Wview);
            }
        });
        OPwindow.addView(Wview, params);
    }

    private String[] setTimeWhell() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, (mouth - 1), 1);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String[] days = new String[maxDay];
        for (int i = 0; i < maxDay; i++) {
            if ((i + 1) < 10) {
                days[i] = "0" + (i + 1) + "日";
            } else {
                days[i] = (i + 1) + "日";
            }

        }
        return days;
    }

    private String[] getMinuteTimeWhell() {
        String[] minutes = new String[60];
        for (int i = 0; i < minutes.length; i++) {
            if (i < 10) {
                minutes[i] = "0" + i + "分";
            } else {
                minutes[i] = i + "分";
            }
        }
        return minutes;
    }

    private String[] getSecondsTimeWhell() {
        String[] seconds = new String[60];
        for (int i = 0; i < seconds.length; i++) {
            if (i < 10) {
                seconds[i] = "0" + i + "秒";
            } else {
                seconds[i] = i + "秒";
            }
        }
        return seconds;
    }

    public void setTextviewColor(String curriteItemText, CityAdapter adapter) {
        ArrayList<View> arrayList = adapter.getArrayList();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextColor(Color.parseColor("#626262"));
            } else {
                textvew.setTextColor(Color.parseColor("#c4c4c4"));
            }
        }
    }
}