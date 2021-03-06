/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.services.data;

import com.sun.research.ws.wadl.HTTPMethods;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.json.simple.JSONObject;
import org.kitodo.data.database.beans.Task;
import org.kitodo.data.database.beans.User;
import org.kitodo.data.database.beans.UserGroup;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.data.database.helper.enums.IndexAction;
import org.kitodo.data.database.persistence.UserGroupDAO;
import org.kitodo.data.elasticsearch.exceptions.CustomResponseException;
import org.kitodo.data.elasticsearch.index.Indexer;
import org.kitodo.data.elasticsearch.index.type.UserGroupType;
import org.kitodo.data.elasticsearch.search.Searcher;
import org.kitodo.data.exceptions.DataException;
import org.kitodo.dto.UserGroupDTO;
import org.kitodo.services.ServiceManager;
import org.kitodo.services.data.base.TitleSearchService;

public class UserGroupService extends TitleSearchService<UserGroup, UserGroupDTO> {

    private UserGroupDAO userGroupDAO = new UserGroupDAO();
    private UserGroupType userGroupType = new UserGroupType();
    private final ServiceManager serviceManager = new ServiceManager();
    private static final Logger logger = LogManager.getLogger(UserGroupService.class);

    /**
     * Constructor with Searcher and Indexer assigning.
     */
    public UserGroupService() {
        super(new Searcher(UserGroup.class));
        this.indexer = new Indexer<>(UserGroup.class);
    }

    @Override
    public List<UserGroupDTO> findAll(String sort, Integer offset, Integer size) throws DataException {
        return convertJSONObjectsToDTOs(findAllDocuments(sort, offset, size), false);
    }

    @Override
    public UserGroup getById(Integer id) throws DAOException {
        return userGroupDAO.find(id);
    }

    @Override
    public List<UserGroup> getAll() {
        return userGroupDAO.findAll();
    }

    @Override
    public Long countDatabaseRows() throws DAOException {
        return userGroupDAO.count("FROM UserGroup");
    }

    @Override
    public Long countDatabaseRows(String query) throws DAOException {
        return userGroupDAO.count(query);
    }

    /**
     * Method saves workpiece object to database.
     *
     * @param userGroup
     *            object
     */
    @Override
    public void saveToDatabase(UserGroup userGroup) throws DAOException {
        userGroupDAO.save(userGroup);
    }

    /**
     * Method saves workpiece document to the index of Elastic Search.
     *
     * @param userGroup
     *            object
     */
    @Override
    @SuppressWarnings("unchecked")
    public void saveToIndex(UserGroup userGroup) throws CustomResponseException, IOException {
        indexer.setMethod(HTTPMethods.PUT);
        if (userGroup != null) {
            indexer.performSingleRequest(userGroup, userGroupType);
        }
    }

    /**
     * Method saves users and tasks related to modified user group.
     *
     * @param userGroup
     *            object
     */
    @Override
    protected void manageDependenciesForIndex(UserGroup userGroup) throws CustomResponseException, IOException {
        manageTasksDependenciesForIndex(userGroup);
        manageUsersDependenciesForIndex(userGroup);
    }

    /**
     * Check if IndexAction flag is delete. If true remove user group from list
     * of user groups and re-save task, if false only re-save task object.
     *
     * @param userGroup
     *            object
     */
    private void manageTasksDependenciesForIndex(UserGroup userGroup) throws CustomResponseException, IOException {
        if (userGroup.getIndexAction() == IndexAction.DELETE) {
            for (Task task : userGroup.getTasks()) {
                task.getUserGroups().remove(userGroup);
                serviceManager.getTaskService().saveToIndex(task);
            }
        } else {
            for (Task task : userGroup.getTasks()) {
                serviceManager.getTaskService().saveToIndex(task);
            }
        }
    }

    /**
     * Check if IndexAction flag is delete. If true remove user group from list
     * of user groups and re-save user, if false only re-save user object.
     *
     * @param userGroup
     *            object
     */
    private void manageUsersDependenciesForIndex(UserGroup userGroup) throws CustomResponseException, IOException {
        if (userGroup.getIndexAction() == IndexAction.DELETE) {
            for (User user : userGroup.getUsers()) {
                user.getUserGroups().remove(userGroup);
                serviceManager.getUserService().saveToIndex(user);
            }
        } else {
            for (User user : userGroup.getUsers()) {
                serviceManager.getUserService().saveToIndex(user);
            }
        }
    }

    /**
     * Method removes user group object from database.
     *
     * @param userGroup
     *            object
     */
    @Override
    public void removeFromDatabase(UserGroup userGroup) throws DAOException {
        userGroupDAO.remove(userGroup);
    }

    /**
     * Method removes user group object from database.
     *
     * @param id
     *            of template object
     */
    @Override
    public void removeFromDatabase(Integer id) throws DAOException {
        userGroupDAO.remove(id);
    }

    /**
     * Method removes user group object from index of Elastic Search.
     *
     * @param userGroup
     *            object
     */
    @Override
    @SuppressWarnings("unchecked")
    public void removeFromIndex(UserGroup userGroup) throws CustomResponseException, IOException {
        indexer.setMethod(HTTPMethods.DELETE);
        if (userGroup != null) {
            indexer.performSingleRequest(userGroup, userGroupType);
        }
    }

    @Override
    public List<UserGroup> getByQuery(String query) throws DAOException {
        return userGroupDAO.search(query);
    }

    /**
     * Refresh user's group object after update.
     *
     * @param userGroup
     *            object
     */
    public void refresh(UserGroup userGroup) {
        userGroupDAO.refresh(userGroup);
    }

    /**
     * Find user groups with exact permissions.
     *
     * @param permission
     *            of the searched user group
     * @return list of JSON objects
     */
    List<JSONObject> findByPermission(Integer permission) throws DataException {
        QueryBuilder query = createSimpleQuery("permission", permission, true);
        return searcher.findDocuments(query.toString());
    }

    /**
     * Find user groups by id of user.
     *
     * @param id
     *            of user
     * @return list of JSON objects with users for specific user group id
     */
    List<JSONObject> findByUserId(Integer id) throws DataException {
        QueryBuilder query = createSimpleQuery("users.id", id, true);
        return searcher.findDocuments(query.toString());
    }

    /**
     * Find user groups by login of user.
     *
     * @param login
     *            of user
     * @return list of search result with user groups for specific user login
     */
    List<JSONObject> findByUserLogin(String login) throws DataException {
        JSONObject user = serviceManager.getUserService().findByLogin(login);
        return findByUserId(getIdFromJSONObject(user));
    }

    /**
     * Get all user groups from index and covert results to format accepted by
     * frontend.
     *
     * @return list of UserGroupDTO objects
     */
    public List<UserGroupDTO> findAll() throws DataException {
        List<JSONObject> jsonObjects = findAllDocuments();
        return convertJSONObjectsToDTOs(jsonObjects, false);
    }

    /**
     * Method adds all object found in database to Elastic Search index.
     */
    @SuppressWarnings("unchecked")
    public void addAllObjectsToIndex() throws CustomResponseException, InterruptedException, IOException {
        indexer.setMethod(HTTPMethods.PUT);
        indexer.performMultipleRequests(getAll(), userGroupType);
    }

    @Override
    public UserGroupDTO convertJSONObjectToDTO(JSONObject jsonObject, boolean related) throws DataException {
        UserGroupDTO userGroupDTO = new UserGroupDTO();
        userGroupDTO.setId(getIdFromJSONObject(jsonObject));
        userGroupDTO.setTitle(getStringPropertyForDTO(jsonObject, "title"));
        userGroupDTO.setUsersSize(getSizeOfRelatedPropertyForDTO(jsonObject, "users"));
        if (!related) {
            userGroupDTO = convertRelatedJSONObjects(jsonObject, userGroupDTO);
        }
        return userGroupDTO;
    }

    private UserGroupDTO convertRelatedJSONObjects(JSONObject jsonObject, UserGroupDTO userGroupDTO) throws DataException {
        userGroupDTO.setUsers(convertRelatedJSONObjectToDTO(jsonObject, "users", serviceManager.getUserService()));
        return userGroupDTO;
    }

    /**
     * Get permission as a string.
     *
     * @param userGroup
     *            object
     * @return permission as a string
     */
    public String getPermissionAsString(UserGroup userGroup) {
        if (userGroup.getPermission() == null) {
            userGroup.setPermission(4);
        } else if (userGroup.getPermission() == 3) {
            userGroup.setPermission(4);
        }
        return String.valueOf(userGroup.getPermission().intValue());
    }

    public void setPermissionAsString(UserGroup userGroup, String permission) {
        userGroup.setPermission(Integer.parseInt(permission));
    }
}
