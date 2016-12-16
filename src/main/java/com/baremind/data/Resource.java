package com.baremind.data;

import java.util.Map;

/**
 * Created by fixopen on 7/12/2016.
 */
public interface Resource extends TransferObject {
    Long getSubjectId();
    Map<String, Object> getContent();


}
