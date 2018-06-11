package com.carefulcollections.gandanga.orbit.Models;

import java.io.Serializable;

/**
 * Created by Gandanga on 2018-06-11.
 */

public class LeaveRequest implements Serializable {
    public int id,employee_id;
    public String off_start_date,off_end_date,off_start_time,leave_type,off_category,off_end_time,reason,name,surname;

    public LeaveRequest(int id, String off_start_date,String off_end_date,String off_start_time,String leave_type,String off_category,String off_end_time,String reason,int employee_id){
        this.id = id;
        this.off_category =off_category;
        this.off_start_date =off_start_date;
        this.off_end_date = off_end_date;
        this.off_end_time = off_end_time;
        this.off_start_time = off_start_time;
        this.off_category = off_category;
        this.reason = reason;
        this.employee_id =employee_id;
        this.leave_type = leave_type;
    }

}
