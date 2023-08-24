create table resadmin(
    raid int primary key auto_increment,
    raname varchar(50),
    rapwd  varchar(50)
);

create table resuser(
    userid int  primary key auto_increment,
    username varchar(50),
    pwd varchar(50),
    email varchar(500)
);

create table resfood(
    fid int  primary key auto_increment,
    fname varchar(50) ,
    normprice numeric(8,2),
    realprice numeric(8,2),
    detail varchar(2000),
    fphoto varchar(1000)
);

create table resorder(
    roid int  primary key auto_increment,
    userid int,
    address varchar(500),
    tel varchar( 100),
    ordertime datetime,
    deliverytime date,
    ps varchar( 2000),
    status int
);

alter table resorder
	add constraint fk_resorder
	     foreign key(userid) references resuser(userid);


create table resorderitem(
    roiid int  primary key auto_increment,
    roid  int,
    fid   int,
    dealprice numeric(8,2),
    num     int
);

alter table resorderitem
    add constraint fk_resorderitem_roid
        foreign key(roid) references resorder( roid);

alter table resorderitem
    add constraint fk_tbl_res_fid
        foreign key( fid ) references resfood( fid);



commit;

