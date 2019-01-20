package com.readyidu.robot.model.business.tv;

import java.util.ArrayList;

/**
 * Created by gx on 2017/12/1.
 */
public class ProgrammeListModel {
    private ArrayList<ProgrammeModel> todayProgram;

    private ArrayList<ProgrammeModel> tommorrowProgram;

    public ArrayList<ProgrammeModel> getTodayProgram() {
        return todayProgram;
    }

    public ArrayList<ProgrammeModel> getTommorrowProgram() {
        return tommorrowProgram;
    }
}