package com.carefulcollections.gandanga.orbit.Models;

/**
 * Created by Gandanga on 2018-05-29.
 */

public class InboxItem {
    public enum ItemType {
        ONE_ITEM, TWO_ITEM;
    }
    public int id,swap_shift,requestor_id,offer_shift,team_member,employee_id;
    public String reason;
    public int approval;
    public String created_at;
    public ItemType type;
    public String shift_date,with_shift;
    public String name,surname;
    public InboxItem(int id,int swap_shift,String with_shift,int offer_shift, int employee_id, int requestor_id, int team_member,String reason,int approval,String name,String surname,String created_at,String shift_date,ItemType type){
        this.id = id;
        this.swap_shift = swap_shift;
        this.with_shift = with_shift;
        this.employee_id = employee_id;
        this.requestor_id = requestor_id;
        this.reason = reason;
        this.approval = approval;
        this.created_at = created_at;
        this.offer_shift = offer_shift;
        this.team_member = team_member;
        this.shift_date = shift_date;
        this.name = name;
        this.surname = surname;
        this.type = type;
    }

    public ItemType getType(){
        return this.type;
    }
}
