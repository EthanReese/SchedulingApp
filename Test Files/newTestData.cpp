#include <fstream>
#include <time.h>
#include <stdlib.h>

using namespace std;

int main(){
    ofstream myFile;
    srand(time(NULL));
    myFile.open("d.txt");
    int id = rand()%100;
    int a=0,b=0,c=0,d=0,e=0,f=0;
    id += 221000;
    string freshmanClasses[20] = {"1001","2707","0842CE","1730A","2301","2031CC","3034CC","2041","1205A","1201A","1281A","1201B","0833","0397A","1162DPPA","1113AAF","1122CT","1905A","11153","0397HNB"};
    //Required 0-4, Math 5-7, Language 8-11 ,Low Electives 11-15 Med Electives 16-19 High Electives 20
    for (int i = 0; i < 90; i++){
        myFile << id << ",";
        for(int j = 0; j < 6; j++){
            myFile << freshmanClasses[j] << ",";
        }
        myFile << freshmanClasses[rand()%3+8] << ",";
        do{
            a = rand()%5+12;
            b = rand()%5+12;
            c = rand()%5+12;
            d = rand()%5+12;
        }while(a == b || a == c || a == d || b == c || b== d || c == d);
        myFile << freshmanClasses[a] << "," << freshmanClasses[b] << "," << freshmanClasses[c] << "," << freshmanClasses[d] << endl;
        id += 3;
    }
    for (int i = 0; i < 120; i++){
        myFile << id << ",";
        for(int j = 0; j < 5; j++){
            myFile << freshmanClasses[j] << ",";
        }
        myFile << freshmanClasses[6] << ",";
        myFile << freshmanClasses[rand()%4+8] << ",";
         do{
            a = rand()%7+12;
            b = rand()%7+12;
            c = rand()%7+12;
            d = rand()%7+12;
        }while(a == b || a == c || a == d || b == c || b== d || c == d);
        myFile << freshmanClasses[a] << "," << freshmanClasses[b] << "," << freshmanClasses[c] << "," << freshmanClasses[d] << endl;
        id += 3;
    }
    for (int i = 0; i < 90; i++){
        myFile << id << ",";
        for(int j = 0; j < 5; j++){
            myFile << freshmanClasses[j] << ",";
        }
        myFile << freshmanClasses[7] << ",";
        myFile << freshmanClasses[rand()%4+8] << ",";
        do{
            a = rand()%6+14;
            b = rand()%6+14;
            c = rand()%6+14;
            d = rand()%6+14;
        }while(a == b || a == c || a == d || b == c || b== d || c == d);

        myFile << freshmanClasses[a] << "," << freshmanClasses[b] << "," << freshmanClasses[c] << "," << freshmanClasses[d] << endl;
        id += 3;
    }
    string sophomoreClasses[23] = {"1002","2721","1731","1401","3034CC","2041","2053","1201B","1205B","1281B","1201C","0833","0397A","1162DPPA","1113AAF","1122CT","1905A","11153","0397HNB","1164A","1162AB","1113BP","0397HNC"};
    // Required 0-3, Math 4-6, Language 7-10 Low 11 - 17 Med 18 - 19 High 20 - 22
    id = 220000 + rand()%100;
    for (int i = 0; i < 90; i++){
        myFile << id << ",";
        for(int j = 0; j < 4; j++){
            myFile << sophomoreClasses[j] << ",";
        }
        myFile << sophomoreClasses[4] << ",";
        myFile << sophomoreClasses[rand()%3+7] << ",";
        do{
            a = rand()%7+11;
            b = rand()%7+11;
            c = rand()%7+11;
            d = rand()%7+11;
        }while(a == b || a == c || a == d || b == c || b== d || c == d);
        myFile << sophomoreClasses[a] << "," << sophomoreClasses[b] << "," << sophomoreClasses[c] << "," << sophomoreClasses[d] << endl;
        id += 3;
    }
    for (int i = 0; i < 120; i++){
        myFile << id << ",";
        for(int j = 0; j < 4; j++){
            myFile << sophomoreClasses[j] << ",";
        }
        myFile << sophomoreClasses[5] << ",";
        myFile << sophomoreClasses[rand()%4+7] << ",";
         do{
            a = rand()%9+11;
            b = rand()%9+11;
            c = rand()%9+11;
            d = rand()%9+11;
        }while(a == b || a == c || a == d || b == c || b== d || c == d);
        myFile << sophomoreClasses[a] << "," << sophomoreClasses[b] << "," << sophomoreClasses[c] << "," << sophomoreClasses[d] << endl;
        id += 3;
    }
    for (int i = 0; i < 90; i++){
        myFile << id << ",";
        for(int j = 0; j < 4; j++){
            myFile << sophomoreClasses[j] << ",";
        }
        myFile << sophomoreClasses[6] << ",";
        myFile << sophomoreClasses[rand()%4+7] << ",";
        do{
            a = rand()%10+13;
            b = rand()%10+13;
            c = rand()%10+13;
            d = rand()%10+13;
        }while(a == b || a == c || a == d || b == c || b== d || c == d);

        myFile << sophomoreClasses[a] << "," << sophomoreClasses[b] << "," << sophomoreClasses[c] << "," << sophomoreClasses[d] << endl;
        id += 3;
    }
    string juniorClasses[26] = {"1003","1012AP","2743AP","1723B","1731U","1715AP","2041","2053","2058AP","1201C","1205C","1201D","0833","1162DPPA","1113AAF","1122CT","11153","0397HNB","1164A","1162AB","1113BP","1164B","0397HNC","1905B","1905H","0397HND"};
    id = 219000 + rand()%100;
    for (int i = 0; i < 90; i++){
        myFile << id << ",";
        myFile << juniorClasses[0] << "," << juniorClasses[5] << "," << juniorClasses[6] << "," << juniorClasses[rand()%2+9] << ",";

        do{
            a = rand()%8+11;
            b = rand()%8+11;
            c = rand()%8+11;
            d = rand()%8+11;
            e = rand()%8+11;
            f = rand()%8+11;
        }while(a == b || a == c || a == d ||a==e || a==f || b == c || b== d || b==e || b==f|| c == d || c==e || c==f || d==e || d==f || e==f);
        myFile << juniorClasses[a] << "," << juniorClasses[b] << "," << juniorClasses[c] << "," << juniorClasses[d] << "," << juniorClasses[e] << "," << juniorClasses[f] << endl;
        id += 3;
    }
    for (int i = 0; i < 120; i++){
        myFile << id << ",";
        myFile << juniorClasses[0] << "," << juniorClasses[rand()%3+3] << "," << juniorClasses[7] << "," << juniorClasses[rand()%3+9] << ",";
         do{
            a = rand()%11+11;
            b = rand()%11+11;
            c = rand()%11+11;
            d = rand()%11+11;
            e = rand()%11+11;
            f = rand()%11+11;
        }while(a == b || a == c || a == d ||a==e || a==f || b == c || b== d || b==e || b==f|| c == d || c==e || c==f || d==e || d==f || e==f);
        myFile << juniorClasses[a] << "," << juniorClasses[b] << "," << juniorClasses[c] << "," << juniorClasses[d] << "," << juniorClasses[e] << "," << juniorClasses[f] << endl;
        id += 3;
    }
    for (int i = 0; i < 90; i++){
        myFile << id << ",";
        myFile << juniorClasses[1] << "," << juniorClasses[2] << "," << juniorClasses[rand()%3+3] << "," << juniorClasses[8] << "," << juniorClasses[rand()%3+9] << ",";
        do{
            a = rand()%15+11;
            b = rand()%15+11;
            c = rand()%15+11;
            d = rand()%15+11;
            e = rand()%15+11;
        }while(a == b || a == c || a == d ||a==e || a==f || b == c || b== d || b==e || b==f|| c == d || c==e || c==f || d==e || d==f || e==f);
        myFile << juniorClasses[a] << "," << juniorClasses[b] << "," << juniorClasses[c] << "," << juniorClasses[d] << "," << juniorClasses[e] << endl;
        id += 3;
    }
}
