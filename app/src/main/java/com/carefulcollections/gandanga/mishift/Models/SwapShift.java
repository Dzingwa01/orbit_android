package com.carefulcollections.gandanga.mishift.Models;

/**
 * Created by Gandanga on 2018-05-29.
 */

public class SwapShift {
    public int id,swap_shift,employee_id,requestor_id;
    public String reason,name,surname;
    public int approval;
    public String created_at,shift_date,with_shift;


    public SwapShift(int id,int swap_shift,String with_shift, int employee_id, int requestor_id, String reason,int approval, String shift_date,String created_at,String name, String surname){
        this.id = id;
        this.swap_shift = swap_shift;
        this.with_shift = with_shift;
        this.employee_id = employee_id;
        this.requestor_id = requestor_id;
        this.reason = reason;
        this.approval = approval;
        this.created_at = created_at;
        this.name = name;
        this.surname = surname;
    }
}
