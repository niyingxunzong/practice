#include "stdafx.h"
using namespace std;
#include <fstream>


int main()
{
	readInode(3);
	/*
	ifstream infile("disk.dat",ios::binary|ios::app);
	inode i ;
	infile.seekg(1024,ios::beg);    //�ƶ��ļ�ָ��
	infile.read((char*)&i,sizeof(i));
	cout<<i.getBlockNum()<<endl;
	*/
	/*
	ifstream inf("disk.dat",ios::binary|ios::app);
	superblock s ;
	inf.read((char*)&s,sizeof(s));
	cout<<s.getBlockNum()<<endl;
	*/
	/*
	ifstream infile("disk.dat",ios::binary);
	infile.read((char*)&sb,sizeof(sb));
	cout<<sb.getBlockNum()<<endl;
	*/
	superblock sb ;    //����һ����������󣬳�פ�ڴ�

	
	printf("                 ********************************************\n");
	printf("                 *            ��ӭʹ�ô��̹���ϵͳ          *\n");
	printf("                 *                                          *\n");
	printf("                 *              1����ʼ������               *\n");
	printf("                 *              2�������û�                 *\n");
	printf("                 *              3���û���¼                 *\n");
    printf("                 *              4���˳�ϵͳ                 *\n");
	printf("                 ********************************************\n");
	while(true)
	{
		bool flg = false;
		printf("��ѡ���Ӧ�����:/>");
		int i = 0;
		cin>>i;
		switch(i)
		{
		case 1:
			//cout<<"��ѡ��1"<<endl;
			initDisk(sb);
			break;
		case 2:
			cout<<"��ѡ��2"<<endl;
			break;
		case 3:
			//cout<<"��ѡ����3"<<endl;
			if(userLogin(sb))    //��½�ɹ�
			{
				while(true)
				{
					cout<<"�����½��Ĳ���"<<endl;
					fileOpation();
					cout<<"��ѡ��/>";
					//char arr[10];
					int arr;
					cin>>arr;
					switch(arr)
					{
					case 1 :
						createFile(sb,"��һ���ļ�");
						break;
					default:
						cout<<"�������,����������!"<<endl;
						break;
					}
				}
			}
			break;
		case 4:
			cout<<"�����˳�ϵͳ��"<<endl;
			exit(0);
			break;
		default :
			cout<<"ѡ���������ѡ��"<<endl;
			break;
		}
		//if(i==1||i==2||i==3||i==4)
		//	break;
		if(flg)
		{
			break;
		}
	}

	return 0;
}