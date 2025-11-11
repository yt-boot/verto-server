package org.jeecg.modules.verto.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.verto.project.entity.VertoProject;
import org.jeecg.modules.verto.project.service.IVertoProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Tag(name = "verto项目管理")
@RestController
@RequestMapping("/verto/project")
@Slf4j
public class ProjectController extends JeecgController<VertoProject, IVertoProjectService> {

    @Autowired
    private IVertoProjectService vertoProjectService;

    /**
     * 分页列表查询
     */
    @Operation(summary = "项目-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<VertoProject>> queryPageList(VertoProject vertoProject,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     HttpServletRequest req) {
        QueryWrapper<VertoProject> queryWrapper = QueryGenerator.initQueryWrapper(vertoProject, req.getParameterMap());
        Page<VertoProject> page = new Page<>(pageNo, pageSize);
        IPage<VertoProject> pageList = vertoProjectService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     */
    @Operation(summary = "项目-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody VertoProject vertoProject) {
        // 开发人员必填校验
        if (vertoProject.getDeveloperId() == null || vertoProject.getDeveloperId().trim().isEmpty()) {
            return Result.error("开发人员不能为空");
        }
        // 缺陷项目必须填写项目描述
        if ("bug".equalsIgnoreCase(vertoProject.getProjectType()) &&
                (vertoProject.getDescription() == null || vertoProject.getDescription().trim().isEmpty())) {
            return Result.error("缺陷项目必须填写项目描述");
        }
        vertoProjectService.save(vertoProject);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     */
    @Operation(summary = "项目-编辑")
    @PutMapping(value = "/edit")
    public Result<String> edit(@RequestBody VertoProject vertoProject) {
        // 开发人员必填校验
        if (vertoProject.getDeveloperId() == null || vertoProject.getDeveloperId().trim().isEmpty()) {
            return Result.error("开发人员不能为空");
        }
        // 缺陷项目必须填写项目描述
        if ("bug".equalsIgnoreCase(vertoProject.getProjectType()) &&
                (vertoProject.getDescription() == null || vertoProject.getDescription().trim().isEmpty())) {
            return Result.error("缺陷项目必须填写项目描述");
        }
        vertoProjectService.updateById(vertoProject);
        return Result.OK("编辑成功！");
    }

    /**
     * 通过id删除
     */
    @Operation(summary = "项目-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id") String id) {
        vertoProjectService.removeById(id);
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除
     */
    @Operation(summary = "项目-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids") String ids) {
        vertoProjectService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     */
    @Operation(summary = "项目-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<VertoProject> queryById(@RequestParam(name = "id") String id) {
        VertoProject vertoProject = vertoProjectService.getById(id);
        if (vertoProject == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(vertoProject);
    }

    /**
     * 导出excel
     */
    @Operation(summary = "项目-导出Excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, VertoProject vertoProject) {
        return super.exportXls(request, vertoProject, VertoProject.class, "verto项目");
    }

    /**
     * 通过excel导入数据
     */
    @Operation(summary = "项目-通过Excel导入数据")
    @PostMapping(value = "/importExcel")
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, VertoProject.class);
    }
}