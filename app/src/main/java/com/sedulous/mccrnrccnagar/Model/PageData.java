package com.sedulous.mccrnrccnagar.Model;

import java.io.Serializable;
import java.util.HashMap;

public class PageData implements Serializable {
        public String response_img1="", response_img2="";
        public String coachNo="", coachName="";
        public HashMap<String, String> answerData=new HashMap<>();//q_id & answer
    }
