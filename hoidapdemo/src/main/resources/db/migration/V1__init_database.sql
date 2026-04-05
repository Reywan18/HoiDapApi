create table admin
(
    id bigint auto_increment primary key,
    email varchar(255) not null,
    ho_ten varchar(255) null,
    password varchar(255) not null,
    constraint UKc0r9atamxvbhjjvy5j8da1kam unique (email)
);

create table cvht
(
    ma_cv varchar(10) not null primary key,
    password varchar(100) not null,
    chuyen_mon varchar(255) null,
    email varchar(255) not null,
    ho_ten varchar(255) not null,
    so_dien_thoai varchar(255) null,
    role varchar(255) null,
    constraint UK30h3lxwxnx2y27dxg3usqitpb unique (email)
);

create table faq
(
    ma_faq bigint auto_increment primary key,
    chu_de varchar(255) null,
    khoa_hoc varchar(255) null,
    khoa_vien varchar(255) null,
    nam_hoc varchar(255) null,
    noi_dung text         null,
    tieu_de varchar(255) null
);

create table lop
(
    ma_cvht varchar(10)  null,
    ma_lop varchar(10)  not null primary key,
    chuyen_nganh varchar(255) null,
    khoa_hoc varchar(255) null,
    constraint FKdpiily4jqlgy4j1kiri2gf1s8 foreign key (ma_cvht) references cvht (ma_cv)
);

create table sinh_vien
(
    ma_lop varchar(10) null,
    ma_sv varchar(10) not null primary key,
    password varchar(100) not null,
    email varchar(255) not null,
    ho_ten varchar(255) not null,
    so_dien_thoai varchar(255) null,
    role varchar(255) null,
    constraint UKh51nnl07xshbecwyda3mwjej6 unique (email),
    constraint FKd6wx9fjodxfkjcdmm3biqagie foreign key (ma_lop) references lop (ma_lop)
);

create table cau_hoi
(
    trangthai int null,
    ma_cau_hoi bigint auto_increment primary key,
    ngaycapnhatcuoi datetime(6) null,
    ngaygui datetime(6) not null,
    ma_cv varchar(10) not null,
    ma_sv varchar(10) not null,
    file_name varchar(255) null,
    file_type varchar(255) null,
    noidung text null,
    tieude varchar(255) null,
    file_data longblob null,
    constraint FKdxp6cvbxxhd51p5lvqbvs88jo foreign key (ma_cv) references cvht (ma_cv),
    constraint FKfwv895kiy8t90ulfv6bkkkl2w foreign key (ma_sv) references sinh_vien (ma_sv),
    check (`trangthai` in (0, 1))
);

create table cau_tra_loi
(
    phien_ban_hien_tai int null,
    id bigint auto_increment primary key,
    ma_cau_hoi bigint null,
    ma_cv varchar(10) null,
    constraint UK68lwrxseut23vdtwbknsfv1jj unique (ma_cau_hoi),
    constraint FKbju612mp7h32fk51vhxfmfye6 foreign key (ma_cau_hoi) references cau_hoi (ma_cau_hoi),
    constraint FKpde8xvfl1e6whn1r83nns9lp2 foreign key (ma_cv) references cvht (ma_cv)
);

create table phien_ban_tra_loi
(
    version int not null,
    answer_id bigint null,
    id bigint auto_increment primary key,
    thoi_gian_tao datetime(6) null,
    file_name varchar(255) null,
    file_type varchar(255) null,
    noi_dung text null,
    file_data longblob null,
    constraint FKnbo1dlfbc5y2v0ids8cl8pxq foreign key (answer_id) references cau_tra_loi (id)
);

