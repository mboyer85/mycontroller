/**
 * Copyright (C) 2015-2016 Jeeva Kandasamy (jkandasa@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mycontroller.standalone.db.dao;

import java.util.HashMap;
import java.util.List;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.2
 */
public interface BaseDao<Tdao, Tid> {
    void create(Tdao tdao);

    void createOrUpdate(Tdao tdao);

    void delete(Tdao tdao);

    void deleteById(Tid id);

    void update(Tdao tdao);

    List<Tdao> getAll();

    Tdao get(Tdao tdao);

    Tdao getById(Tid id);

    void deleteByIds(List<Tid> ids);

    List<Tdao> getAll(List<Tid> ids);

    Long countOf();

    public List<Tdao> getAll(String key, Object value);

    long countOf(HashMap<String, List<Object>> columnValues);

}
