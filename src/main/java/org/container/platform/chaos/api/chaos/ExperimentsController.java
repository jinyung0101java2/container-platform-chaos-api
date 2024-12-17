package org.container.platform.chaos.api.chaos;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.chaos.api.chaos.model.*;
import org.container.platform.chaos.api.common.model.Params;
import org.container.platform.chaos.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Experiments Controller 클래스
 *
 * @author luna
 * @version 1.0
 * @since 2024.06.21
 **/
@Api(value = "ExperimentsController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/experiments")
public class ExperimentsController {
    private final ExperimentsService experimentsService;

    /**
     * Instantiates a new Experiments controller
     *
     * @param experimentsService the Experiments service
     */
    @Autowired
    public ExperimentsController(ExperimentsService experimentsService) {
        this.experimentsService = experimentsService;
    }

    /**
     * Experiments 목록 조회(Get Experiments List)
     *
     * @return the Experiments List
     */
    @ApiOperation(value = "Experiments 목록 조회(Get Experiments List)", nickname = "getExperimentsList")
    @ApiImplicitParams({@ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)})
    @GetMapping
    public ExperimentsList getExperimentsList(Params params) {
        return experimentsService.getExperimentsList(params);
    }

    /**
     * Experiments 상세 조회(Get Experiments Detail)
     *
     * @param params the params
     * @return the experiments detail
     */
    @ApiOperation(value = "Experiments 상세 조회(Get Experiments Detail)", nickname = "getExperiments")
    @ApiImplicitParams({@ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)})
    @GetMapping(value = "/{kind:.+}/{name:.+}")
    public Experiments getExperiments(Params params) {
        return experimentsService.getExperiments(params);
    }

    /**
     * Experiments Status 목록 조회(Get Experiments Status List)
     *
     * @return the Experiments Status List
     */
    @ApiOperation(value = "Experiments Status 목록 조회(Get Experiments Status List)", nickname = "getExperimentsStatusList")
    @ApiImplicitParams({@ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)})
    @GetMapping("/status")
    public ExperimentsDashboardList getExperimentsStatusList(Params params) {
        return experimentsService.getExperimentsStatusList(params);
    }

    /**
     * Experiments 상세 Status 조회(Get Experiments Detail Status)
     *
     * @return the Experiments Detail Status
     */
    @ApiOperation(value = "Experiments 상세 Status 조회(Get Experiments Detail Status)", nickname = "getExperimentsStatus")
    @ApiImplicitParams({@ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)})
    @GetMapping("/status/{uid:.+}")
    public ExperimentsDashboard getExperimentsStatus(Params params) {
        return experimentsService.getExperimentsStatus(params);
    }

    /**
     * Experiments 생성(Create Experiments)
     *
     * @param params the params
     * @return the resultStatus
     */
    @ApiOperation(value = "Experiments 생성(Create Experiments)", nickname = "createExperiments")
    @ApiImplicitParams({@ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)})
    @PostMapping
    public Object createExperiments(@RequestBody Params params) {
        return experimentsService.createExperiments(params);
    }

    /**
     * Experiments 삭제(Delete Experiments)
     *
     * @param params the params
     * @return the resultStatus
     */
    @ApiOperation(value = "Experiments 삭제(Delete Experiments)", nickname = "deleteExperiments")
    @ApiImplicitParams({@ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)})
    @DeleteMapping("/{kind:.+}/{name:.+}")
    public ResultStatus deleteExperiments(Params params) {
        return experimentsService.deleteExperiments(params);
    }

    /**
     * Experiments Events 목록 조회(Get Experiments Events List)
     *
     * @param params the params
     * @return the resultStatus
     */
    @ApiOperation(value = "Experiments Events 목록 조회(Get Experiments Events List)", nickname = "getExperimentsEventsList")
    @ApiImplicitParams({@ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)})
    @GetMapping("/events")
    public ExperimentsEventsList getExperimentsEventsList(Params params) {
        return experimentsService.getExperimentsEventsList(params);
    }

    /**
     * Resource usage by selected Pods during chaos 조회(Get Resource Usage by selected Pods during chaos)
     *
     * @param params the params
     * @return the ResourceUsage
     */
    @ApiOperation(value = "Resource usage by selected Pods during chaos 조회(Get Resource Usage by selected Pods during chaos)", nickname = "getResourceUsageByPod")
    @ApiImplicitParams({@ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)})
    @GetMapping("/resourceUsageByPod/{name:.+}")
    public ResourceUsage getResourceUsageByPod(Params params) {
        return experimentsService.getResourceUsageByPod(params);
    }

    /**
     * Resource usage by workload for selected Pods during chao 조회(Get Resource usage by workload for selected Pods during chao)
     *
     * @param params the params
     * @return the ResourceUsage
     */
    @ApiOperation(value = "Resource usage by workload for selected Pods during chao 조회(Get Resource usage by workload for selected Pods during chao)", nickname = "getResourceUsageByWorkload")
    @ApiImplicitParams({@ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)})
    @GetMapping("/resourceUsageByWorkload/{name:.+}")
    public ResourceUsage getResourceUsageByWorkload(Params params) {
        return experimentsService.getResourceUsageByWorkload(params);
    }

    /**
     * Resource usage by node during chaos 조회(Get Resource usage by node during chaos)
     *
     * @param params the params
     * @return the ResourceUsage
     */
    @ApiOperation(value = "Resource usage by node during chaos 조회(Get Resource usage by node during chaos)", nickname = "getResourceUsageByNode")
    @ApiImplicitParams({@ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body", dataTypeClass = Params.class)})
    @GetMapping("/resourceUsageByNode/{name:.+}")
    public ResourceUsage getResourceUsageByNode(Params params) {
        return experimentsService.getResourceUsageByNode(params);
    }
}


