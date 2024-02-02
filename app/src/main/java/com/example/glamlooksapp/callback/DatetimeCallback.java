package com.example.glamlooksapp.callback;
import com.example.glamlooksapp.utils.Datetime;

import java.util.ArrayList;

public interface DatetimeCallback {
    void onDatetimeFetchComplete(ArrayList<Datetime> datetimes);
}
