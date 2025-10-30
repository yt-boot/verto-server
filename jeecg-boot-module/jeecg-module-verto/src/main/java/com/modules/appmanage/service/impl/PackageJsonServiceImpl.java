package com.verto.modules.appmanage.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.verto.modules.appmanage.entity.AppManage;
import com.verto.modules.appmanage.entity.PackageJsonInfo;
import com.verto.modules.appmanage.service.IAppManageService;
import com.verto.modules.appmanage.service.IPackageJsonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Package.json信息服务实现类
 * 
 * @author verto
 * @since 2024-01-27
 */
@Service
@Slf4j
public class PackageJsonServiceImpl implements IPackageJsonService {
    
    @Autowired
    private IAppManageService appManageService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 模拟数据，实际项目中应该从数据库或Git仓库获取
     */
    private final Map<String, PackageJsonInfo> mockData = initMockData();
    
    @Override
    public PackageJsonInfo getPackageJsonByAppId(String appId) {
        try {
            // 首先尝试从模拟数据获取
            PackageJsonInfo mockInfo = mockData.get(appId);
            if (mockInfo != null) {
                log.info("从模拟数据获取应用 {} 的package.json信息", appId);
                return mockInfo;
            }
            
            // 获取应用信息
            AppManage app = appManageService.getById(appId);
            if (app == null) {
                log.warn("未找到应用ID为 {} 的应用", appId);
                return null;
            }
            
            // 从Git仓库获取package.json信息
            String gitUrl = app.getGitUrl();
            if (gitUrl != null && !gitUrl.isEmpty()) {
                return getPackageJsonFromGit(gitUrl);
            }
            
            log.warn("应用 {} 没有配置Git仓库地址", appId);
            return null;
            
        } catch (Exception e) {
            log.error("获取应用 {} 的package.json信息失败", appId, e);
            return null;
        }
    }
    
    @Override
    public PackageJsonInfo getPackageJsonFromGit(String gitUrl) {
        try {
            // 将GitHub仓库地址转换为raw文件地址
            String rawUrl = convertToRawUrl(gitUrl);
            if (rawUrl == null) {
                log.warn("无法转换Git地址为raw地址: {}", gitUrl);
                return null;
            }
            
            // 获取package.json内容
            String packageJsonContent = fetchPackageJsonContent(rawUrl);
            if (packageJsonContent == null) {
                log.warn("无法获取package.json内容: {}", rawUrl);
                return null;
            }
            
            // 解析package.json内容
            return parsePackageJson(packageJsonContent);
            
        } catch (Exception e) {
            log.error("从Git仓库获取package.json信息失败: {}", gitUrl, e);
            return null;
        }
    }
    
    @Override
    public PackageJsonInfo parsePackageJson(String packageJsonContent) {
        try {
            JsonNode rootNode = objectMapper.readTree(packageJsonContent);
            PackageJsonInfo info = new PackageJsonInfo();
            
            // 基本信息
            info.setName(getTextValue(rootNode, "name"));
            info.setVersion(getTextValue(rootNode, "version"));
            info.setDescription(getTextValue(rootNode, "description"));
            info.setAuthor(getTextValue(rootNode, "author"));
            info.setLicense(getTextValue(rootNode, "license"));
            info.setRepository(getTextValue(rootNode, "repository"));
            info.setHomepage(getTextValue(rootNode, "homepage"));
            info.setBugs(getTextValue(rootNode, "bugs"));
            
            // 依赖信息
            info.setDependencies(parseObjectToMap(rootNode.get("dependencies")));
            info.setDevDependencies(parseObjectToMap(rootNode.get("devDependencies")));
            info.setPeerDependencies(parseObjectToMap(rootNode.get("peerDependencies")));
            info.setOptionalDependencies(parseObjectToMap(rootNode.get("optionalDependencies")));
            info.setScripts(parseObjectToMap(rootNode.get("scripts")));
            
            // 关键词
            JsonNode keywordsNode = rootNode.get("keywords");
            if (keywordsNode != null && keywordsNode.isArray()) {
                String[] keywords = new String[keywordsNode.size()];
                for (int i = 0; i < keywordsNode.size(); i++) {
                    keywords[i] = keywordsNode.get(i).asText();
                }
                info.setKeywords(keywords);
            }
            
            return info;
            
        } catch (Exception e) {
            log.error("解析package.json内容失败", e);
            return null;
        }
    }
    
    /**
     * 将GitHub仓库地址转换为raw文件地址
     */
    private String convertToRawUrl(String gitUrl) {
        if (gitUrl.contains("github.com")) {
            // 将 https://github.com/user/repo.git 转换为 https://raw.githubusercontent.com/user/repo/main/package.json
            String cleanUrl = gitUrl.replace(".git", "");
            String rawUrl = cleanUrl.replace("github.com", "raw.githubusercontent.com") + "/main/package.json";
            return rawUrl;
        }
        // 其他Git服务的处理可以在这里添加
        return null;
    }
    
    /**
     * 获取package.json文件内容
     */
    private String fetchPackageJsonContent(String rawUrl) {
        try {
            URL url = new URL(rawUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                return content.toString();
            } else {
                log.warn("获取package.json失败，响应码: {}", responseCode);
                return null;
            }
            
        } catch (Exception e) {
            log.error("获取package.json内容失败: {}", rawUrl, e);
            return null;
        }
    }
    
    /**
     * 获取JSON节点的文本值
     */
    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null ? fieldNode.asText() : null;
    }
    
    /**
     * 将JSON对象转换为Map
     */
    private Map<String, String> parseObjectToMap(JsonNode node) {
        if (node == null || !node.isObject()) {
            return new HashMap<>();
        }
        
        Map<String, String> map = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            map.put(field.getKey(), field.getValue().asText());
        }
        return map;
    }
    
    /**
     * 初始化模拟数据
     */
    private Map<String, PackageJsonInfo> initMockData() {
        Map<String, PackageJsonInfo> data = new HashMap<>();
        
        // 应用1: Verto平台
        PackageJsonInfo app1 = new PackageJsonInfo();
        app1.setName("verto-platform");
        app1.setVersion("1.0.0");
        Map<String, String> deps1 = new HashMap<>();
        deps1.put("vue", "^3.4.21");
        deps1.put("@vue/runtime-core", "^3.4.21");
        deps1.put("vue-router", "^4.3.0");
        deps1.put("pinia", "^2.1.7");
        deps1.put("ant-design-vue", "^4.1.2");
        deps1.put("@ant-design/icons-vue", "^7.0.1");
        deps1.put("axios", "^1.6.8");
        deps1.put("dayjs", "^1.11.10");
        deps1.put("lodash-es", "^4.17.21");
        deps1.put("vite", "^5.2.0");
        deps1.put("typescript", "^5.4.3");
        app1.setDependencies(deps1);
        
        Map<String, String> devDeps1 = new HashMap<>();
        devDeps1.put("@vitejs/plugin-vue", "^5.0.4");
        devDeps1.put("@typescript-eslint/eslint-plugin", "^7.2.0");
        devDeps1.put("@typescript-eslint/parser", "^7.2.0");
        devDeps1.put("eslint", "^8.57.0");
        devDeps1.put("eslint-plugin-vue", "^9.22.0");
        devDeps1.put("prettier", "^3.2.5");
        devDeps1.put("vite-plugin-mock", "^3.0.1");
        devDeps1.put("vitest", "^1.4.0");
        devDeps1.put("@vue/test-utils", "^2.4.5");
        app1.setDevDependencies(devDeps1);
        
        Map<String, String> peerDeps1 = new HashMap<>();
        peerDeps1.put("vue", "^3.4.0");
        app1.setPeerDependencies(peerDeps1);
        
        data.put("1", app1);
        
        // 应用2: 用户中心
        PackageJsonInfo app2 = new PackageJsonInfo();
        app2.setName("user-center");
        app2.setVersion("2.1.0");
        Map<String, String> deps2 = new HashMap<>();
        deps2.put("react", "^18.2.0");
        deps2.put("react-dom", "^18.2.0");
        deps2.put("react-router-dom", "^6.10.0");
        deps2.put("@reduxjs/toolkit", "^2.2.1");
        deps2.put("react-redux", "^9.1.0");
        deps2.put("antd", "^5.15.3");
        deps2.put("@ant-design/icons", "^5.3.6");
        deps2.put("axios", "^1.6.8");
        deps2.put("moment", "^2.30.1");
        deps2.put("classnames", "^2.5.1");
        app2.setDependencies(deps2);
        
        Map<String, String> devDeps2 = new HashMap<>();
        devDeps2.put("@types/react", "^18.2.66");
        devDeps2.put("@types/react-dom", "^18.2.22");
        devDeps2.put("@vitejs/plugin-react", "^4.2.1");
        devDeps2.put("vite", "^5.2.0");
        devDeps2.put("typescript", "^5.2.2");
        devDeps2.put("eslint", "^8.57.0");
        devDeps2.put("@typescript-eslint/eslint-plugin", "^7.2.0");
        devDeps2.put("@typescript-eslint/parser", "^7.2.0");
        devDeps2.put("eslint-plugin-react-hooks", "^4.6.0");
        devDeps2.put("eslint-plugin-react-refresh", "^0.4.6");
        app2.setDevDependencies(devDeps2);
        
        Map<String, String> peerDeps2 = new HashMap<>();
        peerDeps2.put("react", "^18.0.0");
        peerDeps2.put("react-dom", "^18.0.0");
        app2.setPeerDependencies(peerDeps2);
        
        data.put("2", app2);
        
        // 可以继续添加其他应用的模拟数据...
        
        return data;
    }
}