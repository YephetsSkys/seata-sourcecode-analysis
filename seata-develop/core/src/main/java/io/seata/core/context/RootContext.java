/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.seata.core.context;

import java.util.Map;

import io.seata.common.exception.ShouldNeverHappenException;
import io.seata.common.util.StringUtils;
import io.seata.core.model.BranchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The type Root context.
 *
 * @author slievrly
 */
public class RootContext {

    private RootContext() {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RootContext.class);

    /**
     * The constant KEY_XID.
     */
    public static final String KEY_XID = "TX_XID";

    /**
     * The constant KEY_BRANCH_TYPE
     */
    public static final String KEY_BRANCH_TYPE = "TX_BRANCH_TYPE";

    public static final String KEY_GLOBAL_LOCK_FLAG = "TX_LOCK";

    private static ContextCore CONTEXT_HOLDER = ContextCoreLoader.load();

    /**
     * Gets xid.
     *
     * 得到当前应用运行时的全局事务 XID
     *
     * @return the xid
     */
    public static String getXID() {
        String xid = CONTEXT_HOLDER.get(KEY_XID);
        if (StringUtils.isNotBlank(xid)) {
            return xid;
        }
        return null;
    }

    /**
     * Bind.
     *
     * 将全局事务 XID 绑定到当前应用的运行时中
     *
     * @param xid the xid
     */
    public static void bind(String xid) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("bind {}", xid);
        }
        /** 将全局事务 xid 绑定到当前应用的运行时中 */
        CONTEXT_HOLDER.put(KEY_XID, xid);
    }

    /**
     * declare local transactions will use global lock check for update/delete/insert/selectForUpdate SQL
     */
    public static void bindGlobalLockFlag() {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Local Transaction Global Lock support enabled");
        }

        //just put something not null
        CONTEXT_HOLDER.put(KEY_GLOBAL_LOCK_FLAG, KEY_GLOBAL_LOCK_FLAG);
    }

    /**
     * Unbind string.
     *
     * 将全局事务 XID 从当前应用的运行时中解除绑定，同时将 XID 返回
     *
     * @return the string
     */
    public static String unbind() {
        String xid = CONTEXT_HOLDER.remove(KEY_XID);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("unbind {} ", xid);
        }
        return xid;
    }

    public static void unbindGlobalLockFlag() {
        String lockFlag = CONTEXT_HOLDER.remove(KEY_GLOBAL_LOCK_FLAG);
        if (LOGGER.isDebugEnabled() && lockFlag != null) {
            LOGGER.debug("unbind global lock flag");
        }
    }

    /**
     * In global transaction boolean.
     *
     * 判断当前应用的运行时是否处于全局事务的上下文中
     *
     * @return the boolean
     */
    public static boolean inGlobalTransaction() {
        return CONTEXT_HOLDER.get(KEY_XID) != null;
    }

    /**
     * get the branch type
     *
     * @return the branch type String
     */
    public static String getBranchType() {
        String branchType = CONTEXT_HOLDER.get(KEY_BRANCH_TYPE);
        if (StringUtils.isNotBlank(branchType)) {
            return branchType;
        }
        return null;
    }

    /**
     * bind branch type
     *
     * @param branchType the branch type
     */
    public static void bindBranchType(BranchType branchType) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("bind branch type {}", branchType);
        }

        CONTEXT_HOLDER.put(KEY_BRANCH_TYPE, branchType.name());
    }

    /**
     * unbind branch type
     *
     * @return the previous branch type string
     */
    public static String unbindBranchType() {
        String unbindBranchType = CONTEXT_HOLDER.remove(KEY_BRANCH_TYPE);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("unbind branch type {}", unbindBranchType);
        }
        return unbindBranchType;
    }

    /**
     * requires global lock check
     *
     * @return
     */
    public static boolean requireGlobalLock() {
        return CONTEXT_HOLDER.get(KEY_GLOBAL_LOCK_FLAG) != null;
    }

    /**
     * Assert not in global transaction.
     */
    public static void assertNotInGlobalTransaction() {
        if (inGlobalTransaction()) {
            throw new ShouldNeverHappenException();
        }
    }

    /**
     * entry map
     *
     * @return
     */
    public static Map<String, String> entries() {
        return CONTEXT_HOLDER.entries();
    }
}
