package com.carefulcollections.gandanga.orbit.Models;

/**
 * Created by Gandanga on 2018-05-29.
 */

public class ShiftOffer {
    public int offer_shift,id,team_member,employee_id;
    public String reason,name,surname;
    public int approval;
    public String created_at,shift_date;

    public ShiftOffer(int id, int offer_shift, int team_member,int employee_id,String reason, int approval,String created_at,String shift_date,String name, String surname){
        this.id = id;
        this.offer_shift = offer_shift;
        this.team_member = team_member;
        this.employee_id = employee_id;
        this.reason = reason;
        this.approval = approval;
        this.name = name;
        this.surname = surname;
        this.created_at = created_at;
        this.shift_date = shift_date;
    }
}
