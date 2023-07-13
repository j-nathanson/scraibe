drop database if exists scraibe;
create database scraibe;
use scraibe;

create table app_user (
    app_user_id int primary key auto_increment,
    username varchar(50) not null unique,
    email varchar(50) not null unique,
    password_hash varchar(2048) not null,
    enabled bit not null default(1)
);

create table app_role (
    app_role_id int primary key auto_increment,
    `name` varchar(50) not null unique
);

create table app_user_role (
    app_user_id int not null,
    app_role_id int not null,
    constraint pk_app_user_role
        primary key (app_user_id, app_role_id),
    constraint fk_app_user_role_user_id
        foreign key (app_user_id)
        references app_user(app_user_id),
    constraint fk_app_user_role_role_id
        foreign key (app_role_id)
        references app_role(app_role_id)
);

create table course (
	course_id int primary key auto_increment not null,
	app_user_id int not null,
    `name` varchar(50) not null,
    constraint fk_course_app_user_id
        foreign key (app_user_id)
        references app_user(app_user_id)
);

create table note (
	note_id int primary key auto_increment not null,
    course_id int not null,
    title varchar(100) not null,
    content text not null,
    `date` date not null,
    constraint fk_note_course_id
		foreign key (course_id)
        references course(course_id)
);

create table user_course_note (
	app_user_id int not null,
    course_id int not null,
    note_id int not null,
    constraint pk_user_course_note
		primary key (app_user_id, course_id, note_id),
	constraint fk_user_course_note_app_user_id
		foreign key (app_user_id)
        references app_user(app_user_id),
	constraint fk_user_course_note_course_id
		foreign key (course_id)
        references course(course_id),
	constraint fk_user_course_note_note_id
		foreign key (note_id)
        references note(note_id)
);

insert into app_user (username, email, password_hash, enabled)
values
('sai_shinobi','sai_shinobi@gmail.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', 1),
('justbob', 'justbob@aol.com','$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', 1),
('teacher123', 'teacher123@outlook.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', 1),
('jnathanson', 'jnathanson@dev.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', 1),
('schen', 'schen@dev.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', 1),
('jgonzalez', 'jgonzal@dev.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', 1);

insert into app_role (`name`) values ('user'), ('admin');

insert into app_user_role (app_user_id, app_role_id)
values (1, 1), -- sai_shinobi is a user
(2, 1), -- justbob is a user
(3, 1), -- teacher123 is both a user and an admin
(3, 2),
(4, 1), -- jnathanson, schen, and jgonzalez are all users and admins
(4, 2),
(5, 1),
(5, 2),
(6, 1),
(6, 2); 

insert into course (app_user_id, `name`)
values (1, 'mathematics'), (2, 'physics'), (3, 'chemistry');

insert into note (course_id, title, content, `date`)
values
(1, 'algebraic equations', 'this is a note about algebraic equations.', '2023-06-25'),
(1, 'geometry basics', 'this is a note about geometry basics.', '2023-06-24'),
(2, 'newton''s laws of motion', 'this is a note about newton''s laws of motion.', '2023-06-23'),
(3, 'chemical reactions', 'this is a note about chemical reactions.', '2023-06-22');

insert into user_course_note (app_user_id, course_id, note_id)
values (1, 1, 1), -- sai_shinobi's note for mathematics course
(1, 1, 2), -- sai_shinobi's another note for mathematics course
(2, 2, 3), -- justbob's note for physics course
(3, 3, 4); -- teacher123's note for chemistry course
