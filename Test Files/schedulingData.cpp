#include <iostream>
#include <fstream>
#include <vector>
#include <stdlib.h>
#include <time.h>

using namespace std;

int main(){
    ofstream myFile;
    srand(time(NULL));
    int freshmanCount, sophomoresCount, juniorsCount, seniorsCount;
    int id = 100000;
    int cid = 1001;
    myFile.open("a.txt");
    cout << "How many freshman, sophomores, juniors and seniors do you want" << endl;
    cin >> freshmanCount >> sophomoresCount >> juniorsCount >> seniorsCount;
    for (int i = 0; i < freshmanCount; i++){
        myFile << id << "," << 1001 << "," << 1011 << "," << 1021 << "," << 1031 << "," << 1131 << "," << 1141 <<"," << (((rand()%9)+104)*10)+1 << "," << (((rand()%9)+104)*10)+1 << endl;
        id++;
    }
    id = 200000;
    for (int j = 0; j < sophomoresCount; j++){
        myFile << id << "," << 1002 << "," << 1012 << "," << 1022 << "," << 1032 << "," << 1142 << "," << (((rand()%9)+104)*10)+2 <<"," << (((rand()%9)+104)*10)+2 << "," << (((rand()%9)+104)*10)+2 << endl;
        id++;
    }
    id = 300000;
    for (int k = 0; k < juniorsCount; k++){
        myFile << id << "," << 1003 << "," << 1013 << "," << 1023 << "," << 1033 << "," << (((rand()%9)+104)*10)+3 << "," << (((rand()%9)+104)*10)+3 <<"," << (((rand()%9)+104)*10)+3 << "," << (((rand()%9)+104)*10)+3 << endl;
        id++;
    }
    id = 400000;
    for (int l = 0; l < seniorsCount; l++){
        myFile << id << "," << 1004 << "," << 1014 << "," << 1024 << "," << 1034 << "," << (((rand()%9)+104)*10)+4 << "," << (((rand()%9)+104)*10)+4 <<"," << (((rand()%9)+104)*10)+4 << "," << (((rand()%9)+104)*10)+4 << endl;
        id++;
    }
    myFile.close();
    myFile.open("c.txt");
    for(int m = 0; m < 13; m++){
        if(m < 4){
            for (int n = 0; n < 4; n++){
                myFile << cid << ",true," << 1 << endl;
                cid++;
            }

        }
        else{
            for (int o = 0; o < 4; o++){
                myFile << cid << ",false," << 1 << endl;
                cid++;
            }
        }
        cid+=6;
    }
    myFile << 1131 << ",true," << 1 << endl << 1141 << ",true," << 1 << endl << 1142 << ",true," << 1 << endl;
    myFile.close();
    return 0;






}
