package learn.scraibe.data;

import learn.scraibe.data.mappers.AppUserMapper;
import learn.scraibe.data.mappers.CourseMapper;
import learn.scraibe.models.AppUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class AppUserJdbcTemplateRepository implements AppUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public AppUserJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public AppUser findByUsername(String username) {
        List<String> roles = getRolesByUsername(username);

        final String sql = "select app_user_id, username, email, password_hash, enabled "
                + "from app_user "
                + "where username = ?;";

        AppUser user = jdbcTemplate.query(sql, new AppUserMapper(roles), username)
                .stream()
                .findFirst().orElse(null);

        if (user != null){
            addCourses(user);
        }

        return user;
    }

    @Override
    @Transactional
    public AppUser findByEmail(String email) {
        List<String> roles = getRolesByEmail(email);

        final String sql = "select app_user_id, username, email, password_hash, enabled "
                + "from app_user "
                + "where email = ?;";

        AppUser user = jdbcTemplate.query(sql, new AppUserMapper(roles), email)
                .stream()
                .findFirst().orElse(null);

        if (user != null){
            addCourses(user);
        }

        return user;
    }

    @Override
    @Transactional
    public List<AppUser> getAllUsers() {
        List<String> userIds = getIds();

        final String sql = "select app_user_id, username, email, password_hash, enabled "
                + "from app_user where app_user_id = ?; ";

        List<AppUser> result= new ArrayList<>();

        for (int i = 0; i < userIds.size(); i++) {
            List<String> userRole = getRolesById(Integer.parseInt(userIds.get(i)));
            AppUser user = jdbcTemplate.query(sql, new AppUserMapper(userRole), userIds.get(i))
                    .stream()
                    .findFirst().orElse(null);
            if (user != null){
                addCourses(user);
                result.add(user);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public AppUser create(AppUser user) {

        final String sql = "insert into app_user (username, email, password_hash) values (?,?, ?);";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        user.setAppUserId(keyHolder.getKey().intValue());

        updateRoles(user);

        return user;
    }

    @Override
    @Transactional
    public boolean update(AppUser user) {

        final String sql = "update app_user set "
                + "username = ?, "
                + "email = ?, "
                + "enabled = ? "
                + "where app_user_id = ?;";

        int rowsAffected = jdbcTemplate.update(sql,
                user.getUsername(), user.getEmail(), user.isEnabled(), user.getAppUserId());

        if(rowsAffected == 0){
            return false;
        }
        updateRoles(user);

        return true;
    }

    @Transactional
    private void updateRoles(AppUser user) {
        //move if statement up because collections throws exception when being assigned to null?
        //says Cannot invoke "java.util.Collection.toArray()"
        if (user.getAuthorities() == null) {
            return;
        }
        // delete all roles, then re-add
        jdbcTemplate.update("delete from app_user_role where app_user_id = ?;", user.getAppUserId());

        Collection<GrantedAuthority> authorities = user.getAuthorities();

        for (GrantedAuthority role : authorities) {
            String sql = "insert into app_user_role (app_user_id, app_role_id) "
                    + "select ?, app_role_id from app_role where lower(`name`) = lower(?);";
            jdbcTemplate.update(sql, user.getAppUserId(), role.getAuthority());
        }
    }

    @Override
    @Transactional
    public boolean deleteUser(int userId) {
        String deleteUserCourseNoteSql = "delete from user_course_note where app_user_id = ?";
        int courseNoteRowsAffected = jdbcTemplate.update(deleteUserCourseNoteSql, userId);

        // Delete user's records from app_user_role table
        String deleteUserRolesSql = "delete from app_user_role where app_user_id = ?";
        int userRoleRowsAffected = jdbcTemplate.update(deleteUserRolesSql, userId);

        // Delete notes associated with user
        String deleteNotesSql = "delete from note n " +
                "where n.course_id in (" +
                    "select course_id from course where app_user_id = ?" +
                ")";
        int noteRowsAffected = jdbcTemplate.update(deleteNotesSql, userId);

        String deleteCoursesSql = "delete from course where app_user_id = ?";
        int courseRowsAffected = jdbcTemplate.update(deleteCoursesSql, userId);

        // Delete user's record from app_user table
        String deleteUserSql = "delete from app_user where app_user_id = ?";
        int userRowsAffected = jdbcTemplate.update(deleteUserSql, userId);

        // Return true if all delete operations were successful
        return userRoleRowsAffected > 0 && userRowsAffected > 0;
    }


    private List<String> getRolesByUsername(String username) {
        final String sql = "select r.name "
                + "from app_user_role ur "
                + "inner join app_role r on ur.app_role_id = r.app_role_id "
                + "inner join app_user au on ur.app_user_id = au.app_user_id "
                + "where au.username = ?";
        return jdbcTemplate.query(sql, (rs, rowId) -> rs.getString("name"), username);
    }

    private List<String> getRolesByEmail(String email) {
        final String sql = "select r.name "
                + "from app_user_role ur "
                + "inner join app_role r on ur.app_role_id = r.app_role_id "
                + "inner join app_user au on ur.app_user_id = au.app_user_id "
                + "where au.email = ?";
        return jdbcTemplate.query(sql, (rs, rowId) -> rs.getString("name"), email);
    }

    private List<String> getRolesById(int id) {
        final String sql = "select r.name "
                + "from app_user_role ur "
                + "inner join app_role r on ur.app_role_id = r.app_role_id "
                + "inner join app_user au on ur.app_user_id = au.app_user_id "
                + "where au.app_user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowId) -> rs.getString("name"), id);
    }

    private List<String> getIds(){
        final String sql = "select app_user_id from app_user order by app_user_id;";
        return jdbcTemplate.query(sql, (rs, rowId) -> rs.getString("app_user_id"));
    }

    private void addCourses(AppUser user) {
        final String sql = "select c.course_id, c.`name` from course c " +
                "inner join app_user au on c.app_user_id = au.app_user_id " +
                "where au.app_user_id = ?";
        var courses = jdbcTemplate.query(sql, new CourseMapper(), user.getAppUserId());
        user.setCourses(courses);
    }
}