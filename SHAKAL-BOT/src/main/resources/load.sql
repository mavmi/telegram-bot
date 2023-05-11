delete from request;
delete from "user";
\copy "user" from './cvs/user.cvs' delimiter ',' csv header;
\copy request from './cvs/request.cvs' delimiter ',' csv header;
