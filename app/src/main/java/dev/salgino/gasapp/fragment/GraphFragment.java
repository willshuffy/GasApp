package dev.salgino.gasapp.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dev.salgino.gasapp.R;
import dev.salgino.gasapp.graphing.Controller;
import dev.salgino.gasapp.graphing.Edge;
import dev.salgino.gasapp.graphing.Graph;
import dev.salgino.gasapp.graphing.Vertex;
import dev.salgino.gasapp.model.Assignment;
import dev.salgino.gasapp.model.Station;

public class GraphFragment extends DialogFragment {

    public static final String TAG = "FullScreenDialog";

    Graph graph = Controller.getGraph();
    TextView tvGraph;
    TextView tvConclusion;
    TextView tvVertices[] = new TextView[10];
    TextView tvEdges[][] = new TextView[10][10];

    int algorithm;
    String start, end;

    TextView[][] tvFW = new TextView[12][10];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    public void initMatrix(View view){
        tvFW[10][0] = view.findViewById(R.id.tvVc0);
        tvFW[10][1] = view.findViewById(R.id.tvVc1);
        tvFW[10][2] = view.findViewById(R.id.tvVc2);
        tvFW[10][3] = view.findViewById(R.id.tvVc3);
        tvFW[10][4] = view.findViewById(R.id.tvVc4);
        tvFW[10][5] = view.findViewById(R.id.tvVc5);
        tvFW[10][6] = view.findViewById(R.id.tvVc6);
        tvFW[10][7] = view.findViewById(R.id.tvVc7);
        tvFW[10][8] = view.findViewById(R.id.tvVc8);
        tvFW[10][9] = view.findViewById(R.id.tvVc9);

        tvFW[11][0] = view.findViewById(R.id.tvVl0);
        tvFW[11][1] = view.findViewById(R.id.tvVl1);
        tvFW[11][2] = view.findViewById(R.id.tvVl2);
        tvFW[11][3] = view.findViewById(R.id.tvVl3);
        tvFW[11][4] = view.findViewById(R.id.tvVl4);
        tvFW[11][5] = view.findViewById(R.id.tvVl5);
        tvFW[11][6] = view.findViewById(R.id.tvVl6);
        tvFW[11][7] = view.findViewById(R.id.tvVl7);
        tvFW[11][8] = view.findViewById(R.id.tvVl8);
        tvFW[11][9] = view.findViewById(R.id.tvVl9);

        tvFW[0][0] = view.findViewById(R.id.tv00);
        tvFW[0][1] = view.findViewById(R.id.tv01);
        tvFW[0][2] = view.findViewById(R.id.tv02);
        tvFW[0][3] = view.findViewById(R.id.tv03);
        tvFW[0][4] = view.findViewById(R.id.tv04);
        tvFW[0][5] = view.findViewById(R.id.tv05);
        tvFW[0][6] = view.findViewById(R.id.tv06);
        tvFW[0][7] = view.findViewById(R.id.tv07);
        tvFW[0][8] = view.findViewById(R.id.tv08);
        tvFW[0][9] = view.findViewById(R.id.tv09);

        tvFW[1][0] = view.findViewById(R.id.tv10);
        tvFW[1][1] = view.findViewById(R.id.tv11);
        tvFW[1][2] = view.findViewById(R.id.tv12);
        tvFW[1][3] = view.findViewById(R.id.tv13);
        tvFW[1][4] = view.findViewById(R.id.tv14);
        tvFW[1][5] = view.findViewById(R.id.tv15);
        tvFW[1][6] = view.findViewById(R.id.tv16);
        tvFW[1][7] = view.findViewById(R.id.tv17);
        tvFW[1][8] = view.findViewById(R.id.tv18);
        tvFW[1][9] = view.findViewById(R.id.tv19);

        tvFW[2][0] = view.findViewById(R.id.tv20);
        tvFW[2][1] = view.findViewById(R.id.tv21);
        tvFW[2][2] = view.findViewById(R.id.tv22);
        tvFW[2][3] = view.findViewById(R.id.tv23);
        tvFW[2][4] = view.findViewById(R.id.tv24);
        tvFW[2][5] = view.findViewById(R.id.tv25);
        tvFW[2][6] = view.findViewById(R.id.tv26);
        tvFW[2][7] = view.findViewById(R.id.tv27);
        tvFW[2][8] = view.findViewById(R.id.tv28);
        tvFW[2][9] = view.findViewById(R.id.tv29);

        tvFW[3][0] = view.findViewById(R.id.tv30);
        tvFW[3][1] = view.findViewById(R.id.tv31);
        tvFW[3][2] = view.findViewById(R.id.tv32);
        tvFW[3][3] = view.findViewById(R.id.tv33);
        tvFW[3][4] = view.findViewById(R.id.tv34);
        tvFW[3][5] = view.findViewById(R.id.tv35);
        tvFW[3][6] = view.findViewById(R.id.tv36);
        tvFW[3][7] = view.findViewById(R.id.tv37);
        tvFW[3][8] = view.findViewById(R.id.tv38);
        tvFW[3][9] = view.findViewById(R.id.tv39);

        tvFW[4][0] = view.findViewById(R.id.tv40);
        tvFW[4][1] = view.findViewById(R.id.tv41);
        tvFW[4][2] = view.findViewById(R.id.tv42);
        tvFW[4][3] = view.findViewById(R.id.tv43);
        tvFW[4][4] = view.findViewById(R.id.tv44);
        tvFW[4][5] = view.findViewById(R.id.tv45);
        tvFW[4][6] = view.findViewById(R.id.tv46);
        tvFW[4][7] = view.findViewById(R.id.tv47);
        tvFW[4][8] = view.findViewById(R.id.tv48);
        tvFW[4][9] = view.findViewById(R.id.tv49);

        tvFW[5][0] = view.findViewById(R.id.tv50);
        tvFW[5][1] = view.findViewById(R.id.tv51);
        tvFW[5][2] = view.findViewById(R.id.tv52);
        tvFW[5][3] = view.findViewById(R.id.tv53);
        tvFW[5][4] = view.findViewById(R.id.tv54);
        tvFW[5][5] = view.findViewById(R.id.tv55);
        tvFW[5][6] = view.findViewById(R.id.tv56);
        tvFW[5][7] = view.findViewById(R.id.tv57);
        tvFW[5][8] = view.findViewById(R.id.tv58);
        tvFW[5][9] = view.findViewById(R.id.tv59);

        tvFW[6][0] = view.findViewById(R.id.tv60);
        tvFW[6][1] = view.findViewById(R.id.tv61);
        tvFW[6][2] = view.findViewById(R.id.tv62);
        tvFW[6][3] = view.findViewById(R.id.tv63);
        tvFW[6][4] = view.findViewById(R.id.tv64);
        tvFW[6][5] = view.findViewById(R.id.tv65);
        tvFW[6][6] = view.findViewById(R.id.tv66);
        tvFW[6][7] = view.findViewById(R.id.tv67);
        tvFW[6][8] = view.findViewById(R.id.tv68);
        tvFW[6][9] = view.findViewById(R.id.tv69);

        tvFW[7][0] = view.findViewById(R.id.tv70);
        tvFW[7][1] = view.findViewById(R.id.tv71);
        tvFW[7][2] = view.findViewById(R.id.tv72);
        tvFW[7][3] = view.findViewById(R.id.tv73);
        tvFW[7][4] = view.findViewById(R.id.tv74);
        tvFW[7][5] = view.findViewById(R.id.tv75);
        tvFW[7][6] = view.findViewById(R.id.tv76);
        tvFW[7][7] = view.findViewById(R.id.tv77);
        tvFW[7][8] = view.findViewById(R.id.tv78);
        tvFW[7][9] = view.findViewById(R.id.tv79);

        tvFW[8][0] = view.findViewById(R.id.tv80);
        tvFW[8][1] = view.findViewById(R.id.tv81);
        tvFW[8][2] = view.findViewById(R.id.tv82);
        tvFW[8][3] = view.findViewById(R.id.tv83);
        tvFW[8][4] = view.findViewById(R.id.tv84);
        tvFW[8][5] = view.findViewById(R.id.tv85);
        tvFW[8][6] = view.findViewById(R.id.tv86);
        tvFW[8][7] = view.findViewById(R.id.tv87);
        tvFW[8][8] = view.findViewById(R.id.tv88);
        tvFW[8][9] = view.findViewById(R.id.tv89);

        tvFW[9][0] = view.findViewById(R.id.tv90);
        tvFW[9][1] = view.findViewById(R.id.tv91);
        tvFW[9][2] = view.findViewById(R.id.tv92);
        tvFW[9][3] = view.findViewById(R.id.tv93);
        tvFW[9][4] = view.findViewById(R.id.tv94);
        tvFW[9][5] = view.findViewById(R.id.tv95);
        tvFW[9][6] = view.findViewById(R.id.tv96);
        tvFW[9][7] = view.findViewById(R.id.tv97);
        tvFW[9][8] = view.findViewById(R.id.tv98);
        tvFW[9][9] = view.findViewById(R.id.tv99);
    }

    public void displayFWMatrix(){
        ArrayList<Vertex> vertices = graph.getVertices();
        Integer[][] fwMatrix = graph.floydWarshall();

        List<Assignment> assignmentList = new ArrayList<>();
        Assignment assignment;

        int size = vertices.size();

        for(int i=0;i<size;i++){
            tvFW[10][i].setText(vertices.get(i).getName());
            tvFW[11][i].setText(vertices.get(i).getName());

            for(int j=0;j<size;j++){

                String finalFwMatrix = "~";

                if (!fwMatrix[i][j].toString().equals("9999999"))
                    finalFwMatrix = fwMatrix[i][j].toString();

                tvFW[i][j].setText(finalFwMatrix);
            }
            assignment = new Assignment();
            assignment.setStationName(vertices.get(i).getName());
            assignment.setDistance(tvFW[0][i].getText().toString());
            assignmentList.add(assignment);
        }

        Collections.sort(assignmentList, new Comparator<Assignment>() {
            @Override public int compare(Assignment p1, Assignment p2) {
                return Integer.parseInt( p1.getDistance()) - Integer.parseInt(p2.getDistance()); // Ascending
            }

        });

        String rute = "";
        for(int i=0;i<assignmentList.size();i++) {


            rute += "\n"+i+". "+assignmentList.get(i).getStationName()+"("+assignmentList.get(i).getDistance()+")";

        }

        rute = rute.startsWith("\n") ? rute.substring(1) : rute;

        tvConclusion.setText(rute);

        //Log.e("rute", rute);

    }

    public void initDisplay(View view){

        tvVertices[0] = view.findViewById(R.id.tv0);
        tvVertices[1] = view.findViewById(R.id.tv1);
        tvVertices[2] = view.findViewById(R.id.tv2);
        tvVertices[3] = view.findViewById(R.id.tv3);
        tvVertices[4] = view.findViewById(R.id.tv4);
        tvVertices[5] = view.findViewById(R.id.tv5);
        tvVertices[6] = view.findViewById(R.id.tv6);
        tvVertices[7] = view.findViewById(R.id.tv7);
        tvVertices[8] = view.findViewById(R.id.tv8);
        tvVertices[9] = view.findViewById(R.id.tv9);

        if(graph.isDirected()) {

            //0
            tvEdges[0][1] = view.findViewById(R.id.tvE01);
            tvEdges[0][2] = view.findViewById(R.id.tvE02);
            tvEdges[0][3] = view.findViewById(R.id.tvE03);
            tvEdges[0][4] = view.findViewById(R.id.tvE04);
            tvEdges[0][5] = view.findViewById(R.id.tvE05);
            tvEdges[0][6] = view.findViewById(R.id.tvE06);
            tvEdges[0][7] = view.findViewById(R.id.tvE07);
            tvEdges[0][8] = view.findViewById(R.id.tvE08);
            tvEdges[0][9] = view.findViewById(R.id.tvE09);
            //1
            tvEdges[1][0] = view.findViewById(R.id.tvE10);
            tvEdges[1][2] = view.findViewById(R.id.tvE12);
            tvEdges[1][3] = view.findViewById(R.id.tvE13);
            tvEdges[1][4] = view.findViewById(R.id.tvE14);
            tvEdges[1][5] = view.findViewById(R.id.tvE15);
            tvEdges[1][6] = view.findViewById(R.id.tvE16);
            tvEdges[1][7] = view.findViewById(R.id.tvE17);
            tvEdges[1][8] = view.findViewById(R.id.tvE18);
            tvEdges[1][9] = view.findViewById(R.id.tvE19);
            //2
            tvEdges[2][0] = view.findViewById(R.id.tvE20);
            tvEdges[2][1] = view.findViewById(R.id.tvE21);
            tvEdges[2][3] = view.findViewById(R.id.tvE23);
            tvEdges[2][4] = view.findViewById(R.id.tvE24);
            tvEdges[2][5] = view.findViewById(R.id.tvE25);
            tvEdges[2][6] = view.findViewById(R.id.tvE26);
            tvEdges[2][7] = view.findViewById(R.id.tvE27);
            tvEdges[2][8] = view.findViewById(R.id.tvE28);
            tvEdges[2][9] = view.findViewById(R.id.tvE29);
            //3
            tvEdges[3][0] = view.findViewById(R.id.tvE30);
            tvEdges[3][1] = view.findViewById(R.id.tvE31);
            tvEdges[3][2] = view.findViewById(R.id.tvE32);
            tvEdges[3][4] = view.findViewById(R.id.tvE34);
            tvEdges[3][5] = view.findViewById(R.id.tvE35);
            tvEdges[3][6] = view.findViewById(R.id.tvE36);
            tvEdges[3][7] = view.findViewById(R.id.tvE37);
            tvEdges[3][8] = view.findViewById(R.id.tvE38);
            tvEdges[3][9] = view.findViewById(R.id.tvE39);
            //4
            tvEdges[4][0] = view.findViewById(R.id.tvE40);
            tvEdges[4][1] = view.findViewById(R.id.tvE41);
            tvEdges[4][2] = view.findViewById(R.id.tvE42);
            tvEdges[4][3] = view.findViewById(R.id.tvE43);
            tvEdges[4][5] = view.findViewById(R.id.tvE45);
            tvEdges[4][6] = view.findViewById(R.id.tvE46);
            tvEdges[4][7] = view.findViewById(R.id.tvE47);
            tvEdges[4][8] = view.findViewById(R.id.tvE48);
            tvEdges[4][9] = view.findViewById(R.id.tvE49);
            //5
            tvEdges[5][0] = view.findViewById(R.id.tvE50);
            tvEdges[5][1] = view.findViewById(R.id.tvE51);
            tvEdges[5][2] = view.findViewById(R.id.tvE52);
            tvEdges[5][3] = view.findViewById(R.id.tvE53);
            tvEdges[5][4] = view.findViewById(R.id.tvE54);
            tvEdges[5][6] = view.findViewById(R.id.tvE56);
            tvEdges[5][7] = view.findViewById(R.id.tvE57);
            tvEdges[5][8] = view.findViewById(R.id.tvE58);
            tvEdges[5][9] = view.findViewById(R.id.tvE59);
            //6
            tvEdges[6][0] = view.findViewById(R.id.tvE60);
            tvEdges[6][1] = view.findViewById(R.id.tvE61);
            tvEdges[6][2] = view.findViewById(R.id.tvE62);
            tvEdges[6][3] = view.findViewById(R.id.tvE63);
            tvEdges[6][4] = view.findViewById(R.id.tvE64);
            tvEdges[6][5] = view.findViewById(R.id.tvE65);
            tvEdges[6][7] = view.findViewById(R.id.tvE67);
            tvEdges[6][8] = view.findViewById(R.id.tvE68);
            tvEdges[6][9] = view.findViewById(R.id.tvE69);
            //7
            tvEdges[7][0] = view.findViewById(R.id.tvE70);
            tvEdges[7][1] = view.findViewById(R.id.tvE71);
            tvEdges[7][2] = view.findViewById(R.id.tvE72);
            tvEdges[7][3] = view.findViewById(R.id.tvE73);
            tvEdges[7][4] = view.findViewById(R.id.tvE74);
            tvEdges[7][5] = view.findViewById(R.id.tvE75);
            tvEdges[7][6] = view.findViewById(R.id.tvE76);
            tvEdges[7][8] = view.findViewById(R.id.tvE78);
            tvEdges[7][9] = view.findViewById(R.id.tvE79);
            //8
            tvEdges[8][0] = view.findViewById(R.id.tvE80);
            tvEdges[8][1] = view.findViewById(R.id.tvE81);
            tvEdges[8][2] = view.findViewById(R.id.tvE82);
            tvEdges[8][3] = view.findViewById(R.id.tvE83);
            tvEdges[8][4] = view.findViewById(R.id.tvE84);
            tvEdges[8][5] = view.findViewById(R.id.tvE85);
            tvEdges[8][6] = view.findViewById(R.id.tvE86);
            tvEdges[8][7] = view.findViewById(R.id.tvE87);
            tvEdges[8][9] = view.findViewById(R.id.tvE89);
            //9
            tvEdges[9][0] = view.findViewById(R.id.tvE90);
            tvEdges[9][1] = view.findViewById(R.id.tvE91);
            tvEdges[9][2] = view.findViewById(R.id.tvE92);
            tvEdges[9][3] = view.findViewById(R.id.tvE93);
            tvEdges[9][4] = view.findViewById(R.id.tvE94);
            tvEdges[9][5] = view.findViewById(R.id.tvE95);
            tvEdges[9][6] = view.findViewById(R.id.tvE96);
            tvEdges[9][7] = view.findViewById(R.id.tvE97);
            tvEdges[9][8] = view.findViewById(R.id.tvE98);
        }else{

            //0
            tvEdges[0][1] = view.findViewById(R.id.tvE01);
            tvEdges[0][2] = view.findViewById(R.id.tvE02);
            tvEdges[0][3] = view.findViewById(R.id.tvE03);
            tvEdges[0][4] = view.findViewById(R.id.tvE04);
            tvEdges[0][5] = view.findViewById(R.id.tvE05);
            tvEdges[0][6] = view.findViewById(R.id.tvE06);
            tvEdges[0][7] = view.findViewById(R.id.tvE07);
            tvEdges[0][8] = view.findViewById(R.id.tvE08);
            tvEdges[0][9] = view.findViewById(R.id.tvE09);
            //1
            tvEdges[1][0] = view.findViewById(R.id.tvE01);
            tvEdges[1][2] = view.findViewById(R.id.tvE12);
            tvEdges[1][3] = view.findViewById(R.id.tvE13);
            tvEdges[1][4] = view.findViewById(R.id.tvE14);
            tvEdges[1][5] = view.findViewById(R.id.tvE15);
            tvEdges[1][6] = view.findViewById(R.id.tvE16);
            tvEdges[1][7] = view.findViewById(R.id.tvE17);
            tvEdges[1][8] = view.findViewById(R.id.tvE18);
            tvEdges[1][9] = view.findViewById(R.id.tvE19);
            //2
            tvEdges[2][0] = view.findViewById(R.id.tvE02);
            tvEdges[2][1] = view.findViewById(R.id.tvE12);
            tvEdges[2][3] = view.findViewById(R.id.tvE23);
            tvEdges[2][4] = view.findViewById(R.id.tvE24);
            tvEdges[2][5] = view.findViewById(R.id.tvE25);
            tvEdges[2][6] = view.findViewById(R.id.tvE26);
            tvEdges[2][7] = view.findViewById(R.id.tvE27);
            tvEdges[2][8] = view.findViewById(R.id.tvE28);
            tvEdges[2][9] = view.findViewById(R.id.tvE29);
            //3
            tvEdges[3][0] = view.findViewById(R.id.tvE03);
            tvEdges[3][1] = view.findViewById(R.id.tvE13);
            tvEdges[3][2] = view.findViewById(R.id.tvE23);
            tvEdges[3][4] = view.findViewById(R.id.tvE34);
            tvEdges[3][5] = view.findViewById(R.id.tvE35);
            tvEdges[3][6] = view.findViewById(R.id.tvE36);
            tvEdges[3][7] = view.findViewById(R.id.tvE37);
            tvEdges[3][8] = view.findViewById(R.id.tvE38);
            tvEdges[3][9] = view.findViewById(R.id.tvE39);
            //4
            tvEdges[4][0] = view.findViewById(R.id.tvE04);
            tvEdges[4][1] = view.findViewById(R.id.tvE14);
            tvEdges[4][2] = view.findViewById(R.id.tvE24);
            tvEdges[4][3] = view.findViewById(R.id.tvE34);
            tvEdges[4][5] = view.findViewById(R.id.tvE45);
            tvEdges[4][6] = view.findViewById(R.id.tvE46);
            tvEdges[4][7] = view.findViewById(R.id.tvE47);
            tvEdges[4][8] = view.findViewById(R.id.tvE48);
            tvEdges[4][9] = view.findViewById(R.id.tvE49);
            //5
            tvEdges[5][0] = view.findViewById(R.id.tvE05);
            tvEdges[5][1] = view.findViewById(R.id.tvE15);
            tvEdges[5][2] = view.findViewById(R.id.tvE25);
            tvEdges[5][3] = view.findViewById(R.id.tvE35);
            tvEdges[5][4] = view.findViewById(R.id.tvE45);
            tvEdges[5][6] = view.findViewById(R.id.tvE56);
            tvEdges[5][7] = view.findViewById(R.id.tvE57);
            tvEdges[5][8] = view.findViewById(R.id.tvE58);
            tvEdges[5][9] = view.findViewById(R.id.tvE59);
            //6
            tvEdges[6][0] = view.findViewById(R.id.tvE06);
            tvEdges[6][1] = view.findViewById(R.id.tvE16);
            tvEdges[6][2] = view.findViewById(R.id.tvE26);
            tvEdges[6][3] = view.findViewById(R.id.tvE36);
            tvEdges[6][4] = view.findViewById(R.id.tvE46);
            tvEdges[6][5] = view.findViewById(R.id.tvE56);
            tvEdges[6][7] = view.findViewById(R.id.tvE67);
            tvEdges[6][8] = view.findViewById(R.id.tvE68);
            tvEdges[6][9] = view.findViewById(R.id.tvE69);
            //7
            tvEdges[7][0] = view.findViewById(R.id.tvE07);
            tvEdges[7][1] = view.findViewById(R.id.tvE17);
            tvEdges[7][2] = view.findViewById(R.id.tvE27);
            tvEdges[7][3] = view.findViewById(R.id.tvE37);
            tvEdges[7][4] = view.findViewById(R.id.tvE47);
            tvEdges[7][5] = view.findViewById(R.id.tvE57);
            tvEdges[7][6] = view.findViewById(R.id.tvE67);
            tvEdges[7][8] = view.findViewById(R.id.tvE78);
            tvEdges[7][9] = view.findViewById(R.id.tvE79);
            //8
            tvEdges[8][0] = view.findViewById(R.id.tvE08);
            tvEdges[8][1] = view.findViewById(R.id.tvE18);
            tvEdges[8][2] = view.findViewById(R.id.tvE28);
            tvEdges[8][3] = view.findViewById(R.id.tvE38);
            tvEdges[8][4] = view.findViewById(R.id.tvE48);
            tvEdges[8][5] = view.findViewById(R.id.tvE58);
            tvEdges[8][6] = view.findViewById(R.id.tvE68);
            tvEdges[8][7] = view.findViewById(R.id.tvE78);
            tvEdges[8][9] = view.findViewById(R.id.tvE89);
            //9
            tvEdges[9][0] = view.findViewById(R.id.tvE09);
            tvEdges[9][1] = view.findViewById(R.id.tvE19);
            tvEdges[9][2] = view.findViewById(R.id.tvE29);
            tvEdges[9][3] = view.findViewById(R.id.tvE39);
            tvEdges[9][4] = view.findViewById(R.id.tvE49);
            tvEdges[9][5] = view.findViewById(R.id.tvE59);
            tvEdges[9][6] = view.findViewById(R.id.tvE69);
            tvEdges[9][7] = view.findViewById(R.id.tvE79);
            tvEdges[9][8] = view.findViewById(R.id.tvE89);

            //Setting background image
            //0
            tvEdges[0][1].setBackgroundResource(R.drawable.und01);
            tvEdges[0][2].setBackgroundResource(R.drawable.und02);
            tvEdges[0][3].setBackgroundResource(R.drawable.und03);
            tvEdges[0][4].setBackgroundResource(R.drawable.und04);
            tvEdges[0][5].setBackgroundResource(R.drawable.und05);
            tvEdges[0][6].setBackgroundResource(R.drawable.und06);
            tvEdges[0][7].setBackgroundResource(R.drawable.und07);
            tvEdges[0][8].setBackgroundResource(R.drawable.und08);
            tvEdges[0][9].setBackgroundResource(R.drawable.und09);
            //1
            tvEdges[1][2].setBackgroundResource(R.drawable.und12);
            tvEdges[1][3].setBackgroundResource(R.drawable.und13);
            tvEdges[1][4].setBackgroundResource(R.drawable.und14);
            tvEdges[1][5].setBackgroundResource(R.drawable.und15);
            tvEdges[1][6].setBackgroundResource(R.drawable.und16);
            tvEdges[1][7].setBackgroundResource(R.drawable.und17);
            tvEdges[1][8].setBackgroundResource(R.drawable.und18);
            tvEdges[1][9].setBackgroundResource(R.drawable.und19);
            //2
            tvEdges[2][3].setBackgroundResource(R.drawable.und23);
            tvEdges[2][4].setBackgroundResource(R.drawable.und24);
            tvEdges[2][5].setBackgroundResource(R.drawable.und25);
            tvEdges[2][6].setBackgroundResource(R.drawable.und26);
            tvEdges[2][7].setBackgroundResource(R.drawable.und27);
            tvEdges[2][8].setBackgroundResource(R.drawable.und28);
            tvEdges[2][9].setBackgroundResource(R.drawable.und29);
            //3
            tvEdges[3][4].setBackgroundResource(R.drawable.und34);
            tvEdges[3][5].setBackgroundResource(R.drawable.und35);
            tvEdges[3][6].setBackgroundResource(R.drawable.und36);
            tvEdges[3][7].setBackgroundResource(R.drawable.und37);
            tvEdges[3][8].setBackgroundResource(R.drawable.und38);
            tvEdges[3][9].setBackgroundResource(R.drawable.und39);
            //4
            tvEdges[4][5].setBackgroundResource(R.drawable.und45);
            tvEdges[4][6].setBackgroundResource(R.drawable.und46);
            tvEdges[4][7].setBackgroundResource(R.drawable.und47);
            tvEdges[4][8].setBackgroundResource(R.drawable.und48);
            tvEdges[4][9].setBackgroundResource(R.drawable.und49);
            //5
            tvEdges[5][6].setBackgroundResource(R.drawable.und56);
            tvEdges[5][7].setBackgroundResource(R.drawable.und57);
            tvEdges[5][8].setBackgroundResource(R.drawable.und58);
            tvEdges[5][9].setBackgroundResource(R.drawable.und59);
            //6
            tvEdges[6][7].setBackgroundResource(R.drawable.und67);
            tvEdges[6][8].setBackgroundResource(R.drawable.und68);
            tvEdges[6][9].setBackgroundResource(R.drawable.und69);
            //7
            tvEdges[7][8].setBackgroundResource(R.drawable.und78);
            tvEdges[7][9].setBackgroundResource(R.drawable.und79);
            //8
            tvEdges[8][9].setBackgroundResource(R.drawable.und89);

        }
    }

    public void displayGraph(){
        ArrayList<Vertex> vertices;
        Graph graphAux = new Graph();

        switch (algorithm){
            case 1:
                graphAux = graph.transitiveClosure();
                break;
        }
        vertices = graphAux.getVertices();

        for(int i=0;i<vertices.size();i++){
            tvVertices[i].setText(vertices.get(i).getName());
            tvVertices[i].setVisibility(View.VISIBLE);

            for (int j = 0; j < vertices.size(); j++) {
                Edge edge = graphAux.findEdge(vertices.get(i), vertices.get(j));
                if (edge != null) {

                    tvEdges[i][j].setText(edge.getWeight() +""); // using ' +"" ' as a easy form of conversion from int to String
                    tvEdges[i][j].setVisibility(View.VISIBLE);
                }
            }
        }

//        for(int i=0;i<vertices.size();i++) {
//            for (int j = 0; j < vertices.size(); j++) {
//                Edge edge = graphAux.findEdge(vertices.get(i), vertices.get(j));
//                if (edge != null) {
//
//                    tvEdges[i][j].setText(edge.getWeight() +""); // using ' +"" ' as a easy form of conversion from int to String
//                    tvEdges[i][j].setVisibility(View.VISIBLE);
//                }
//            }
//        }

        tvGraph.setText(graphAux.printGraph());

    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        super.onCreateView(inflater, parent, state);

        View view = getActivity().getLayoutInflater().inflate(R.layout.view_graph, parent, false);
        tvGraph = view.findViewById(R.id.tvGraph);
        tvConclusion = view.findViewById(R.id.tvConclusion);

        Intent i1 = getActivity().getIntent();
        algorithm = 1;
        start = i1.getStringExtra("start");
        end = i1.getStringExtra("end");

        initDisplay(view);
        initMatrix(view);
        displayFWMatrix();
        displayGraph();

        return view;
    }
}