package com.njust.service;

import com.njust.common.ServerResponse;
import com.njust.pojo.Category;

import java.util.List;

public interface ICategoryService {
    ServerResponse<String> addcategory(Integer parentId, String categoryName );
    ServerResponse<String>  updateCategoryName(String categoryName,Integer categoryId);
    ServerResponse<List<Category>> getAllChildCategoryInfo(Integer parentId);
    ServerResponse<List<Integer>> getCategoryAndDeepCategory(Integer categoryId);
}
