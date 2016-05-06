#include "stdafx.h"

#include <fstream>
using namespace std;

int MAX_INODE_BLOCK_NUM = ((MAX_INODE_NUM * SINGLE_INODE_SIZE)/BLOCK_SIZE);     //inode �ڵ���ռ�õĴ��̿���Ŀ

/*��ʼ�������ļ�*/
int initDisk(superblock &sb)   
{
	ifstream infile("disk.dat",ios::binary);     //���ִ򿪷�ʽ����ļ������ڲ����½����ᱨ����������ص������ģ������ļ��Ƿ����
	if(!infile)
	{
		cout<<"�����ļ��ϲ�����,ģ�⿪ʼ,�����ļ����ڴ���..."<<endl;
		ofstream outfile("disk.dat",ios::binary);     //����򿪵��ļ������ڣ��ͻ��½�һ��
		if(outfile)     //�ļ��������򿪳ɹ�
		{
			cout<<"�����ļ�������ɣ�"<<endl;
			while(true)
			{
				char c = 'Y';
				cout<<"�Ƿ��ʽ��:(Y/N)";
				cin>>c;
				switch (c)
				{
				case 'Y':
					formatDisk(sb);
					break;
				case 'N':
					break;
				default:
					cout<<"ѡ�����"<<endl;
				}
				if(c=='Y'||c=='N')    //����û�����ѡ���ˣ��˳�����ѭ��
				{
					break;
				}		
			}
			outfile.close();
		}
		else     //ģ������ļ�����ʧ�ܣ��˳���û�н�����ȥ�������ˣ������˳�
		{
			cout<<"init disk error !"<<endl;
			exit(0);
		}
	}
	else    //��������ļ�һ��ʼ�ʹ���
	{
		infile.seekg(0,ios::end);    //�ļ�ָ��ָ���ļ�ĩβ
		//cout<<infile.tellg()<<endl;     //
		if(!infile.tellg())
		{
			cout<<"�����ļ���δ��ʽ���������ʽ��..."<<endl;
		}
		else
		{
			//���볬�����ܵ�����
			infile.seekg(0,ios::beg);
			infile.read((char*)&sb,sizeof(sb));
			/*
			cout<<sb.getBlockNum()<<endl;
			cout<<"�û�����"<<sb.getUserNum()<<endl;
			cout<<"��һ���û���Ϊ��"<<sb.getUser()[0].getName()<<endl;
			*/
			cout<<"�����д������ݣ���ʽ��������..."<<endl;
		}
		infile.close();    //�ļ��ر�
		while(true)
		{
			char c = 'Y';
			cout<<"�Ƿ�--��ʽ��:(Y/N)";
			cin>>c;
			switch (c)
			{
			case 'Y':
				formatDisk(sb);
				break;
			case 'N':
				break;
			default:
				cout<<"ѡ�����"<<endl;
			}
			if(c=='Y'||c=='N')    //����û�����ѡ���ˣ��˳�����ѭ��
			{
				break;
			}	
		}
	}
	return 1;
}

/*���̸�ʽ��*/
int formatDisk(superblock &sb)     //��ʽ�����̽���д����̵�д�룬��������̵�һЩ��¼
{
	cout<<"��ʽ����ʼ!"<<endl;
	sb.setBlockNum(DISK_SIZE/BLOCK_SIZE);     //���д��̿���
	sb.setFreeBlockNum(DISK_SIZE/BLOCK_SIZE);    //���д��̿���
	sb.setInodeNum(MAX_INODE_NUM);    //���е�inode����Ŀ
	sb.setFreeInodeNum(MAX_INODE_NUM);    //����inode����Ŀ
	sb.setInodeStartBlockNum(1);     //����ӵڶ��ſ鿪ʼ��ţ�Ҳ����1�ſ�
	sb.setInodeBlockNum(MAX_INODE_BLOCK_NUM);    //�궨������������Ŀ���
	sb.setDirectoryInodeId(0);     //�������õ�һ��inode ��Ŀ¼�ļ���inode
	sb.setUserNum(0);    //��ǰ��û���û�

	sb.initFreeBlockBitMap();    //��ʼ�����п�λʾͼ
	sb.initFreeInodeBitMap();    //��ʼ�����нڵ�λʾͼ

	//���濪ʼ���������ļ�
	ofstream outfile("disk.dat",ios::binary);
	outfile.seekp(0,ios::beg);    //�ļ�ָ��ָ��ʼλ�� 

	//������д�����֮ǰ���޸Ĵ��̿��пռ����Ŀ   �ͱ�Ǵ��̵�λʾͼ
	sb.setFreeBlockNum(sb.getFreeBlockNum()-1);    //�����鱾��ռ��һ�����̿�  ���Կհ׿�����һ��
	sb.changeBit(sb.getFreeBlockBitMap(),0);    //��λʾͼ��һ������λ��Ϊ1 ��ʾ�ѱ�ռ��
	//cout<<"���п�λʾͼ��"<<sb.getFreeBlockBitMap()[0]<<endl;


	
	//����Ŀ¼�ļ�����Ϊ�䴴��inode�ڵ�   �޸�inode�ڵ�λʾͼ   д�ڵڶ������̿���
	inode dirInode;
	dirInode.setId(0);    //��0��inode������Ŀ¼�ļ���id
	dirInode.setBlockNum(0);

	for(int i = MAX_INODE_BLOCK_NUM+1;i<sb.getBlockNum();i++)    //�������ļ��洢�ӳ������inode���ʼ
	{
		//����֮��Ŀ����ҳ�һ����еĿ飬�����Ŀ¼�ļ�
		if(!sb.isOne(sb.getFreeInodeBitMap(),i))
		{
			//cout<<"cao ---------"<<i<<endl;
			//�ҵ����д��̿飬ռ���� ���޸���λʾͼ�е���Ϣ
			sb.changeBit(sb.getFreeInodeBitMap(),i);    //λʾͼ������0��ʼ
			sb.setFreeBlockNum(sb.getFreeBlockNum()-1);    //���д��̿�����һ
			sb.setFreeInodeNum(sb.getFreeInodeNum()-1);    //���нڵ�����һ
			dirInode.addAddr(i);    //�ѵ�ַ�����ӵ�inode��
			break;    //ֻ��һ�����ҵ�������ѭ��
		}
	}
	sb.changeBit(sb.getFreeInodeBitMap(),0);    //��λʾͼ��һ������λ��Ϊ1 ��ʾ��inode�ѱ�ռ��

	//cout<<"�ж�ĳλ�Ƿ�Ϊ1��"<<sb.isOne(sb.getFreeBlockBitMap(),44)<<endl;

	//�ڸ�ʽ��ʱ����һ�������û�root ����root 
	user u("root","root");
	sb.addUser(u);
	//cout<<"�ļ�ָ��λ�ã�"<<outfile.tellp()<<endl;
	outfile.write((char*)&sb,sizeof(sb));    //��������д����̿��е�һ��     Ҳ����0�ſ�
	//cout<<"sizeof(sb)"<<sizeof(sb)<<endl;
	//cout<<"�ļ�ָ��λ�ã�"<<outfile.tellp()<<endl;
	


	//��inode��Ϣд�뵽������   �����Ϊ����ģ���������д������Ҫ�����ļ�ָ���λ��
	int offset = BLOCK_SIZE * 1 + dirInode.getId()*sizeof(inode);     //
	outfile.seekp(0,ios::beg);    //�ļ�ָ��ָ��ʼλ�� 

	//cout<<"offect "<<offset<<endl;
	//cout<<"�ļ�ָ��λ�ã�"<<outfile.tellp()<<endl;
	outfile.seekp(offset,ios::beg);    //�ļ�ָ���ƶ���inode����λ��
	//cout<<"�ļ�ָ��λ�ã�"<<outfile.tellp()<<endl;
	outfile.write((char*)&dirInode,sizeof(dirInode));
	//cout<<"sizeof(dirInode)"<<sizeof(dirInode)<<endl;
	//cout<<"�ļ�ָ��λ�ã�"<<outfile.tellp()<<endl;

	outfile.close();

	/*
	inode ii ;
	ifstream ifs("disk.dat",ios::binary|ios::app);
	ifs.seekg(offset,ios::beg);
	ifs.read((char*)&ii,sizeof(ii));
	cout<<ii.getBlockNum()<<endl;
	*/

	cout<<"��ʽ�����!"<<endl;
	return 1;
}

/*�ļ�����*/
int createFile(superblock &sb,char * filename)
{
	//���Ȳ��ҳ������е� ���п�Ϳ��нڵ����Ƿ��Ѿ�ռ��
	if(sb.getFreeBlockNum()<=0 || sb.getFreeInodeNum()<=0)
	{
		cout<<"��ǰ�ռ����������ɷ��䣡"<<endl;
		return 0;
	}
	else    //���д��̷����inode�ڵ�ķ���
	{
		//�ȷ���inode�ڵ�
		for(int j = 0;j<sb.getInodeNum();j++)
		{
			//�ж�λʾͼ����λ�Ƿ����
			bool isTrue = false;
			isTrue = sb.isOne(sb.getFreeInodeBitMap(),j);
			if(!isTrue)    //������У�ռ����
			{
				cout<<"�����ļ������д˴η����i�ڵ�Ľڵ�ţ�"<<j<<endl;
				sb.changeBit(sb.getFreeInodeBitMap(),j);     //�޸Ŀ��нڵ�λʾͼ
				inode newInode ;
				newInode.setId(j);    //id���Ǵ�0��ʼ������
				newInode.setFileSize(0);    //�ļ�Ϊ��
				newInode.setBlockNum(0);
				for(int i = MAX_INODE_BLOCK_NUM+1;i<sb.getBlockNum();i++)    //�������ļ��洢�ӳ������inode���ʼ
				{
					//����֮��Ŀ����ҳ�һ����еĿ飬������ļ�
					if(!sb.isOne(sb.getFreeBlockBitMap(),i))
					{
						cout<<"�����ļ������д˴η���Ŀ��п�Ľڵ�ţ�"<<i<<endl;
						//�ҵ����д��̿飬ռ���� ���޸���λʾͼ�е���Ϣ
						sb.changeBit(sb.getFreeBlockBitMap(),i);    //λʾͼ������0��ʼ
						sb.setFreeBlockNum(sb.getFreeBlockNum()-1);    //���д��̿�����һ
						sb.setFreeInodeNum(sb.getFreeInodeNum()-1);    //���нڵ�����һ
						newInode.addAddr(i);    //�ѵ�ַ�����ӵ�inode��
						break;
					}
				}

				//�����������Ϣд���ļ�
				ofstream outfile("disk.dat",ios::binary);
				outfile.seekp(0,ios::beg);    //�ļ�ָ����ڿ�ʼλ��
				cout<<"��ǰ�ļ�ָ�룺"<<outfile.tellp()<<endl;
				outfile.write((char*)&sb,sizeof(sb));
				cout<<"д���ļ���С��"<<sizeof(sb)<<endl;
				cout<<"��ǰ�ļ�ָ�룺"<<outfile.tellp()<<endl;

				//��inode��Ϣд�뵽������   �����Ϊ����ģ���������д������Ҫ�����ļ�ָ���λ��
				int offset = BLOCK_SIZE * 1 + newInode.getId()*sizeof(inode);     //
				cout<<"offset:"<<offset<<endl;
				cout<<"��ǰ�ļ�ָ�룺"<<outfile.tellp()<<endl;
				outfile.seekp(offset,ios::beg);    //�ļ�ָ���ƶ���inode����λ��
				cout<<"ָ��ƫ��֮��ǰ�ļ�ָ�룺"<<outfile.tellp()<<endl;
				outfile.write((char*)&newInode,sizeof(newInode));
				cout<<"д���ļ���ǰ�ļ�ָ�룺"<<outfile.tellp()<<endl;
				outfile.close();

				ifstream inf("disk.dat",ios::binary);
				inode i ;
				inf.seekg(offset,ios::beg);
				inf.read((char*)&i,sizeof(i));
				cout<<"id--"<<i.getId()<<endl;

				//writeToFCB(filename,newInode.getUser(),newInode.getId());    //���ļ���Ϣд��Ŀ¼����

				break;    //����ѭ��
			}
		}
	}
}

/*д��FCB*/
int writeToFCB(char * filename ,char * owener,int inodeId)
{
	FCB fcb ;    //
	fcb.setFileName(filename);
	fcb.setOwener(owener);
	fcb.setInodeId(inodeId);

	//����Ŀ¼�ļ�����Ӧ��inode�ڵ㣬Ȼ�����inode�ļ��Ĵ��  ���̿�
	int offset = BLOCK_SIZE*1 ;
	ifstream infile("disk.dat",ios::app);
	inode i ;
	infile.seekg(offset,ios::beg);    //�ƶ��ļ�ָ��
	infile.read((char*)&i,sizeof(i));
	infile.close();
	
	cout<<"�ļ����ȣ�"<<i.getFileSize()<<endl;
	cout<<"�ļ���ռ�Ŀ�����"<<i.getBlockNum()<<endl;
	cout<<"�ļ����ڴ��̿��:"<<i.getAddr()[0]<<endl;    //�������Ŀ¼�ļ�ֻռ��һ����̿�

	//��FCBд��Ŀ¼�ļ�
	ofstream outfile("disk.dat",ios::binary);
	offset = i.getAddr()[0] * BLOCK_SIZE + inodeId * sizeof(fcb);
	outfile.seekp(0,ios::beg);
	outfile.seekp(offset,ios::beg);
	outfile.write((char*)&fcb,sizeof(fcb));
	outfile.close();

	return 1;
}

//��ȡinode
inode * readInode(int inodeId)
{
	int offset = BLOCK_SIZE*1 + inodeId * sizeof(inode) ;

	cout<<"offset:"<<offset<<endl;
	ifstream infile("disk.dat",ios::binary);
	inode i ;
	infile.seekg(offset,ios::beg);    //�ƶ��ļ�ָ��
	infile.read((char*)&i,sizeof(i));

	cout<<""<<i.getBlockNum()<<endl;
	cout<<"___"<<i.getAddr()[0]<<endl;

	return &i;
}

/*�����û�*/
int createAccount(char * username ,char * password,superblock &sup)
{
	user newUser(username,password);
	sup.addUser(newUser);
	return 1;
}

/*�û���¼*/
int userLogin(superblock &sb)
{
	cout<<"��ǰϵͳ�û����£���ѡ��/>"<<endl;
	for(int i=0;i<sb.getUserNum();i++)
	{
		cout<<sb.getUser()[i].getName()<<endl;
	}
	cout<<"��ѡ���¼�û���/>";
	char username[USER_NAME_SIZE] = "";
	char pass[USER_PASS_SIZE] = "" ;
	cin>>username;
	user u ;    //����һ��user������ʱ����û���Ϣ
	bool isExit = false;    //�Ƿ���ڴ��û�
	for(int j = 0;j<sb.getUserNum();j++)
	{
		if(strcmp(sb.getUser()[j].getName(),username)==0)
		{
			u.setName(username);
			u.setPass(sb.getUser()[j].getPass());
			isExit = true;
		}
	}
	while(!isExit)
	{
		cout<<"�û�������������ѡ��/>";
		cin>>username;
		for(int j = 0;j<sb.getUserNum();j++)
		{
			if(strcmp(sb.getUser()[j].getName(),username)==0)
			{
				u.setName(username);
				u.setPass(sb.getUser()[j].getPass());
				isExit = true;
			}
		}
	}

	if(isExit)
	{
		while(true)    //��¼��֤
		{
			cout<<"����������/>";
			cin>>pass;
			if(strcmp(pass,u.getPass())==0)    //����������ȷ
			{
				cout<<"��¼�ɹ�---"<<endl;
				system("cls");
				//fileOpation();
				break;
			}
			else    //�����������
			{
				cout<<"���������������!"<<endl;
			}
		}
	}
	return 1;
}

/*�ļ�����ѡ��*/
void fileOpation()
{
	cout<<"------------------1.�����ļ�------------------"<<endl;
	cout<<"------------------2.ɾ���ļ�------------------"<<endl;
	cout<<"------------------3.���ļ�------------------"<<endl;
	cout<<"------------------4.д���ļ�------------------"<<endl;
	cout<<"------------------5.�����ļ�------------------"<<endl;
	cout<<"-----------------6.�޸��ļ���------------------"<<endl;

}