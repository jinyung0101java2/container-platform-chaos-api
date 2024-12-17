package org.container.platform.chaos.api.chaos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.container.platform.chaos.api.chaos.model.*;
import org.container.platform.chaos.api.common.*;
import org.container.platform.chaos.api.common.model.Params;
import org.container.platform.chaos.api.common.model.ResultStatus;
import org.container.platform.chaos.api.chaos.model.StressChaos;
import org.container.platform.chaos.api.workloads.pods.PodsList;
import org.container.platform.chaos.api.workloads.pods.support.HorizontalPodAutoscalerItem;
import org.container.platform.chaos.api.workloads.pods.support.HorizontalPodAutoscalerList;
import org.container.platform.chaos.api.workloads.replicaSets.ReplicaSetsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.apache.logging.log4j.util.Strings.isEmpty;
import static org.springframework.vault.support.DurationParser.parseDuration;

/**
 * Experiments Service 클래스
 *
 * @author luna
 * @version 1.0
 * @since 2024.06.21
 */
@Service
public class ExperimentsService {

    private final CommonService commonService;
    private final RestTemplateService restTemplateService;
    private final PropertyService propertyService;
    private final TemplateService templateService;

    /**
     * Instantiates a new Experiments service
     *
     * @param restTemplateService the rest template service
     * @param commonService       common service
     * @param propertyService     the property service
     * @param templateService     the template service
     */

    @Autowired
    public ExperimentsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService, TemplateService templateService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
        this.templateService = templateService;
    }

    /**
     * ExperimentsList 목록 조회 (Get Experiments List)
     *
     * @param params the params
     * @return the ExperimentsList
     */
    public ExperimentsList getExperimentsList(Params params) {
        ExperimentsList experimentsList = new ExperimentsList();

        HashMap responseMapPodFault = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosPodFaultsPodKillListUrl(), HttpMethod.GET, null, Map.class, params);
        ExperimentsList podFaultList = commonService.setResultObject(responseMapPodFault, ExperimentsList.class);
        if (podFaultList != null) {
            experimentsList.setItems(podFaultList.getItems());
        }

        HashMap responseMapNetworkDelay = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosNetworkFaultsDelayListUrl(), HttpMethod.GET, null, Map.class, params);
        ExperimentsList networkDelayList = commonService.setResultObject(responseMapNetworkDelay, ExperimentsList.class);
        if (networkDelayList != null) {
            experimentsList.getItems().addAll(networkDelayList.getItems());
        }

        HashMap responseMapStress = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosStressScenariosListUrl(), HttpMethod.GET, null, Map.class, params);
        ExperimentsList stressList = commonService.setResultObject(responseMapStress, ExperimentsList.class);
        if (stressList != null) {
            experimentsList.getItems().addAll(stressList.getItems());
        }

        if (experimentsList.getItems() != null) {
            for (ExperimentsListItems item : experimentsList.getItems()) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                ZonedDateTime utcDateTime = ZonedDateTime.parse(item.getMetadata().getCreationTimestamp(), formatter);
                ZonedDateTime seoulDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String creationTime = seoulDateTime.format(outputFormatter);

                item.getMetadata().setCreationTimestamp(creationTime);
            }
        }
        experimentsList = commonService.resourceListProcessing(experimentsList, params, ExperimentsList.class);

        return (ExperimentsList) commonService.setResultModel(experimentsList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Experiments 상세 조회(Get Experiments Detail)
     *
     * @param params the params
     * @return the experiments detail
     */
    public Experiments getExperiments(Params params) {
        Experiments experiments = new Experiments();

        if (params.kind.equals("PodChaos")) {
            HashMap responseMapPodFault = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosPodFaultsPodKillGetUrl(), HttpMethod.GET, null, Map.class, params);
            ExperimentsItem podFaultItem = commonService.setResultObject(responseMapPodFault, ExperimentsItem.class);
            experiments.addItem(podFaultItem);
        }
        if (params.kind.equals("NetworkChaos")) {
            HashMap responseMapNetworkDelay = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosNetworkFaultsDelayGetUrl(), HttpMethod.GET, null, Map.class, params);
            ExperimentsItem networkDelayList = commonService.setResultObject(responseMapNetworkDelay, ExperimentsItem.class);
            experiments.addItem(networkDelayList);
        }

        if (params.kind.equals("StressChaos")) {
            HashMap responseMapStress = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosStressScenariosGetUrl(), HttpMethod.GET, null, Map.class, params);
            ExperimentsItem stressList = commonService.setResultObject(responseMapStress, ExperimentsItem.class);

            LocalDateTime startTime = LocalDateTime.ofInstant(Instant.parse(stressList.getMetadata().getCreationTimestamp()), ZoneId.systemDefault());
            LocalDateTime endTime = LocalDateTime.ofInstant(Instant.parse(calculateEndTime(stressList.getMetadata().getCreationTimestamp(), stressList.getSpec().getDuration())), ZoneId.systemDefault());
            Duration duration = Duration.between(startTime, endTime);
            long durationInMillis = duration.toMillis();
            stressList.setExperimentTime(durationInMillis);

            experiments.addItem(stressList);
        }

        if (experiments.getItems().size() > 0) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            ZonedDateTime utcDateTime = ZonedDateTime.parse(experiments.getItems().get(0).getMetadata().getCreationTimestamp(), formatter);
            ZonedDateTime seoulDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String creationTime = seoulDateTime.format(outputFormatter);

            experiments.getItems().get(0).getMetadata().setCreationTimestamp(creationTime);
        }

        return (Experiments) commonService.setResultModel(experiments, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * ExperimentsList Status 목록 조회 (Get Experiments Status List)
     *
     * @param params the params
     * @return the ExperimentsStatusList
     */
    public ExperimentsDashboardList getExperimentsStatusList(Params params) {
        ObjectMapper mapper = new ObjectMapper();
        ExperimentsDashboardList experimentsDashboardList = new ExperimentsDashboardList();

        List<ExperimentsDashboardListItems> items = mapper.convertValue(
                restTemplateService.send(Constants.TARGET_CHAOS_API,
                        propertyService.getChaosApiExperimentListUrl(),
                        HttpMethod.GET, null, ArrayList.class, params),
                new TypeReference<List<ExperimentsDashboardListItems>>() {
                }
        );
        List<ExperimentsDashboardListItems> newItems = new ArrayList<>();

        if (items != null) {
            for (ExperimentsDashboardListItems item : items) {
                for (Object uid : params.getStatusList()) {
                    if (Objects.equals(uid, item.getUid())) {
                        newItems.add(item);
                        break;
                    }
                }
            }
        }

        experimentsDashboardList.setItems(newItems);

        return (ExperimentsDashboardList) commonService.setResultModel(experimentsDashboardList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * ExperimentsList 상세 Status 조회 (Get Experiments Detail Status)
     *
     * @param params the params
     * @return the ExperimentsDashboard
     */
    public ExperimentsDashboard getExperimentsStatus(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CHAOS_API,
                propertyService.getChaosApiExperimentGetUrl(), HttpMethod.GET, null, Map.class, params);

        ExperimentsDashboard experimentsDashboard = commonService.setResultObject(responseMap, ExperimentsDashboard.class);

        return (ExperimentsDashboard) commonService.setResultModel(experimentsDashboard, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Experiments 생성(Create Experiments)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createExperiments(Params params) {
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();
        ResultStatus resultStatus = null;

        Map map = new HashMap();
        map.put("kind", params.getKind());
        map.put("name", params.getName());
        map.put("namespace", params.getChaosNamespace());
        map.put("action", params.getAction());
        map.put("gracePeriod", params.getGracePeriod());
        map.put("duration", params.getDuration());
        map.put("latency", params.getLatency());

        stringBuilder.append(templateService.convert("create_chaosMesh_common.ftl", map));
        stringBuilder.append(Constants.NEW_LINE);
        stringBuilder.append(Constants.CHAOS_MESH_NAMESPACES);

        for (int i = 0; i < params.getNamespaces().size(); i++) {
            line = "      - " + params.getNamespaces().get(i) + Constants.NEW_LINE;
            stringBuilder.append(line);
        }

        stringBuilder.append(Constants.CHAOS_MESH_LABEL_SELECTOR);

        Map<String, String> mapLabelSelector = params.getLabelSelectors();

        for (Map.Entry<String, String> entry : mapLabelSelector.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            line = "      " + key + ": " + value + Constants.NEW_LINE;
            stringBuilder.append(line);
        }

        stringBuilder.append(Constants.CHAOS_MESH_PODS);

        Map<String, ArrayList<String>> mapPods = objectMapper.convertValue(params.getPods(), Map.class);
        for (Map.Entry<String, ArrayList<String>> entry : mapPods.entrySet()) {
            String key = entry.getKey();
            line = "      " + key + ":";
            stringBuilder.append(line);
            stringBuilder.append(Constants.NEW_LINE);

            ArrayList<String> value = entry.getValue();
            for (int i = 0; i < value.size(); i++) {
                line = "        - " + value.get(i);
                stringBuilder.append(line);
                stringBuilder.append(Constants.NEW_LINE);
            }
        }


        if (params.getKind().equals(Constants.CHAOS_MESH_KIND_POD_CHAOS)) {
            stringBuilder.append(templateService.convert("create_podFaults_podKill.ftl", map));
            params.setYaml(stringBuilder.toString());
            resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosPodFaultsPodKillCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        } else if (params.getKind().equals(Constants.CHAOS_MESH_KIND_NETWORK_CHAOS)) {
            stringBuilder.append(templateService.convert("create_networkFaults_delay.ftl", map));
            params.setYaml(stringBuilder.toString());
            resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosNetworkFaultsDelayCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        } else if (params.getKind().equals(Constants.CHAOS_MESH_KIND_STRESS_CHAOS)) {
            stringBuilder.append(templateService.convert("create_stressScenarios.ftl", map));
            stringBuilder.append(Constants.NEW_LINE);

            Map<String, Object> mapStressors = objectMapper.convertValue(params.getStressors(), Map.class);
            for (Map.Entry<String, Object> entry : mapStressors.entrySet()) {
                if (entry.getKey().equals(Constants.CHAOS_MESH_STRESSORS_CPU)) {
                    line = "    " + Constants.CHAOS_MESH_STRESSORS_CPU + ":";
                    stringBuilder.append(line);
                    stringBuilder.append(Constants.NEW_LINE);

                    Object value = entry.getValue();
                    Map<String, Integer> mapCpu = objectMapper.convertValue(value, Map.class);
                    for (Map.Entry<String, Integer> entryCpu : mapCpu.entrySet()) {
                        stringBuilder.append("      " + entryCpu.getKey() + ": " + entryCpu.getValue());
                        stringBuilder.append(Constants.NEW_LINE);
                    }
                }
                if (entry.getKey().equals(Constants.CHAOS_MESH_STRESSORS_MEMORY)) {
                    line = "    " + Constants.CHAOS_MESH_STRESSORS_MEMORY + ":";
                    stringBuilder.append(line);
                    stringBuilder.append(Constants.NEW_LINE);

                    Object value = entry.getValue();
                    Map<String, Integer> mapMemory = objectMapper.convertValue(value, Map.class);
                    for (Map.Entry<String, Integer> entryMemory : mapMemory.entrySet()) {
                        stringBuilder.append("      " + entryMemory.getKey() + ": " + entryMemory.getValue());
                        stringBuilder.append(Constants.NEW_LINE);
                    }
                }
            }
            params.setYaml(stringBuilder.toString());
            resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosStressScenariosCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

            // stresschaos DB 등록
            StressChaos stressChaos = getStressChaos(params);
            LocalDateTime startTime = LocalDateTime.ofInstant(Instant.parse(stressChaos.getCreationTime()), ZoneId.systemDefault());
            LocalDateTime endTime = LocalDateTime.ofInstant(Instant.parse(calculateEndTime(stressChaos.getCreationTime(), params.getDuration())), ZoneId.systemDefault());
            Duration duration = Duration.between(startTime, endTime);
            long durationInMillis = duration.toMillis();

            if (durationInMillis >= 30000) {
                StressChaos resultStatusDB = createStressChaosData(params);
                if (!resultStatusDB.getResultCode().equals("SUCCESS")) {
                    params.setNamespace(params.getChaosNamespace());
                    ResultStatus resultStatusDelete = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                            propertyService.getCpMasterApiChaosStressScenariosDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);

                    return (ResultStatus) commonService.setResultModel(resultStatusDB, Constants.RESULT_STATUS_FAIL);
                } else {
                    ResultStatus resultStatusCollector = restTemplateService.send(Constants.TARGET_CHAOS_COLLECTOR_API,
                            "/scheduler", HttpMethod.POST, params, ResultStatus.class, params);

                    if (!resultStatusCollector.getResultCode().equals("SUCCESS")) {
                        return (ResultStatus) commonService.setResultModel(resultStatusCollector, Constants.RESULT_STATUS_FAIL);
                    }
                }
            }
        }
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Experiments 삭제(Delete Experiments)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteExperiments(Params params) {
        ResultStatus resultStatus = null;
        if (params.kind.equals("PodChaos")) {
            resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosPodFaultsPodKillDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        }
        if (params.kind.equals("NetworkChaos")) {
            resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosNetworkFaultsDelayDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        }
        if (params.kind.equals("StressChaos")) {
            resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiChaosStressScenariosDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);

            ResultStatus deleteDBStressChaos = deleteStressChaos(params);
            if (!deleteDBStressChaos.getHttpStatusCode().equals(200)) {
                ResultStatus resultModel = (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
                resultModel.setDetailMessage("chaos는 삭제되었으나, Metric가 정상적으로 삭제되지 않았습니다. 관리자에게 문의하세요.");

                return resultModel;
            }
        }
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);

    }

    /**
     * StressChaos DB 삭제(Delete StressChaos DB)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteStressChaos(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_COMMON_API,
                "/chaos/stressChaos/{chaosName}".replace("{chaosName}", params.getName()), HttpMethod.DELETE, null, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ExperimentsList Events 목록 조회 (Get Experiments Events List)
     *
     * @param params the params
     * @return the ExperimentsEventsList
     */
    public ExperimentsEventsList getExperimentsEventsList(Params params) {
        ObjectMapper mapper = new ObjectMapper();
        ExperimentsEventsList experimentsEventsList = new ExperimentsEventsList();

        if (isEmpty(params.getObject_id())) {

            if (params.getNamespace().equalsIgnoreCase(Constants.ALL_NAMESPACES)) {
                List<ExperimentsEventsListItems> items = mapper.convertValue(
                        restTemplateService.send(Constants.TARGET_CHAOS_API,
                                propertyService.getChaosApiEventListUrl(),
                                HttpMethod.GET, null, ArrayList.class, params),
                        new TypeReference<List<ExperimentsEventsListItems>>() {
                        }
                );

                for (ExperimentsEventsListItems item : items) {
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                    ZonedDateTime utcDateTime = ZonedDateTime.parse(item.getCreated_at(), formatter);
                    ZonedDateTime seoulDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String creationTime = seoulDateTime.format(outputFormatter);

                    item.setCreated_at(creationTime);
                }

                experimentsEventsList.setItems(items);
                experimentsEventsList = commonService.resourceListProcessing(experimentsEventsList, params, ExperimentsEventsList.class);
                return (ExperimentsEventsList) commonService.setResultModel(experimentsEventsList, Constants.RESULT_STATUS_SUCCESS);
            } else {
                String nsfieldSelector = "?namespace=" + params.getNamespace();

                List<ExperimentsEventsListItems> items = mapper.convertValue(
                        restTemplateService.send(Constants.TARGET_CHAOS_API,
                                propertyService.getChaosApiEventListUrl() + nsfieldSelector,
                                HttpMethod.GET, null, ArrayList.class, params),
                        new TypeReference<List<ExperimentsEventsListItems>>() {
                        }
                );

                if (items != null) {
                    for (ExperimentsEventsListItems item : items) {
                        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                        ZonedDateTime utcDateTime = ZonedDateTime.parse(item.getCreated_at(), formatter);
                        ZonedDateTime seoulDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
                        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String creationTime = seoulDateTime.format(outputFormatter);

                        item.setCreated_at(creationTime);
                    }
                }

                experimentsEventsList.setItems(items);
                experimentsEventsList = commonService.resourceListProcessing(experimentsEventsList, params, ExperimentsEventsList.class);
                return (ExperimentsEventsList) commonService.setResultModel(experimentsEventsList, Constants.RESULT_STATUS_SUCCESS);
            }
        } else {
            String uidfieldSelector = "?object_id=" + params.getObject_id();
            List<ExperimentsEventsListItems> items = mapper.convertValue(
                    restTemplateService.send(Constants.TARGET_CHAOS_API,
                            propertyService.getChaosApiEventListUrl() + uidfieldSelector,
                            HttpMethod.GET, null, ArrayList.class, params),
                    new TypeReference<List<ExperimentsEventsListItems>>() {
                    }
            );

            for (ExperimentsEventsListItems item : items) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                ZonedDateTime utcDateTime = ZonedDateTime.parse(item.getCreated_at(), formatter);
                ZonedDateTime seoulDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String creationTime = seoulDateTime.format(outputFormatter);

                item.setCreated_at(creationTime);
            }

            experimentsEventsList.setItems(items);
            experimentsEventsList = commonService.resourceListProcessing(experimentsEventsList, params, ExperimentsEventsList.class);
            return (ExperimentsEventsList) commonService.setResultModel(experimentsEventsList, Constants.RESULT_STATUS_SUCCESS);
        }
    }

    /**
     * StressChaos Resources Data 생성(Create StressChaos Resources Data)
     *
     * @param params the params
     * @return the ResultStatus
     */
    public StressChaos createStressChaosData(Params params) {
        StressChaos stressChaos = getStressChaos(params);
        StressChaos resultStatus = restTemplateService.sendGlobal(Constants.TARGET_COMMON_API,
                "/chaos/stressChaos", HttpMethod.POST, stressChaos, StressChaos.class, params);

        return (StressChaos) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Stress Chaos 정보 조회  (Get Stress Chaos info)
     *
     * @param params the params
     * @return the ResultStatus
     */
    public StressChaos getStressChaos(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiChaosStressScenariosGetUrl(), HttpMethod.GET, null, Map.class, params);
        ExperimentsItem experimentsItem = commonService.setResultObject(responseMap, ExperimentsItem.class);
        StressChaos stressChaos = setStressChaos(experimentsItem);

        return stressChaos;
    }


    /**
     * StressChaos 값 설정  (Set StressChaos)
     *
     * @return the stressChaos
     * @StressChaos StressChaos the StressChaos
     */
    public StressChaos setStressChaos(ExperimentsItem experimentsItem) {
        StressChaos stressChaos = new StressChaos();
        stressChaos.setChaosName(experimentsItem.getMetadata().getName());
        stressChaos.setNamespaces((String) experimentsItem.getSpec().getSelector().getNamespaces().get(0));
        stressChaos.setCreationTime(experimentsItem.getMetadata().getCreationTimestamp());
        stressChaos.setDuration(experimentsItem.getSpec().getDuration());
        stressChaos.setEndTime(calculateEndTime(experimentsItem.getMetadata().getCreationTimestamp(), experimentsItem.getSpec().getDuration()));

        return stressChaos;
    }


    /**
     * EndTime 계산 (Calculate EndTime)
     *
     * @return the endTime`
     * @creationTime creationTime the creationTime
     * @duration duration the duration
     */
    public String calculateEndTime(String creationTime, String duration) {
        ZonedDateTime creationDateTime = ZonedDateTime.parse(creationTime, DateTimeFormatter.ISO_DATE_TIME);
        Duration durationParsed = parseDuration(duration);
        ZonedDateTime endDateTime = creationDateTime.plus(durationParsed);

        return endDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }


    /**
     * Resource usage by selected Pods during chaos 조회(Get Resource Usage by selected Pods during chaos)
     *
     * @param params the params
     * @return the ResourceUsage
     */
    public ResourceUsage getResourceUsageByPod(Params params) {
        HashMap horizontalpodautoscalers = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListAutoscalingListUrl(), HttpMethod.GET, null, Map.class, params);
        HorizontalPodAutoscalerList horizontalPodAutoscalerList = commonService.setResultObject(horizontalpodautoscalers, HorizontalPodAutoscalerList.class);
        boolean isHPA = false;

        for (HorizontalPodAutoscalerItem horizontalPodAutoscalerItem : horizontalPodAutoscalerList.getItems()) {
            String hpaTargetName = horizontalPodAutoscalerItem.getSpec().getScaleTargetRef().getName();
            String hpaTargetKind = horizontalPodAutoscalerItem.getSpec().getScaleTargetRef().getKind();

            for (Object podName : params.getPodList()) {
                HashMap podHashMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                        propertyService.getCpMasterApiListPodsGetUrl(), HttpMethod.GET, null, Map.class, params);
                PodsList podList = commonService.setResultObject(podHashMap, PodsList.class);
                String podOwnerKind = podList.getItems().get(0).getMetadata().getOwnerReferences().get(0).getKind();
                String podOwnerName = podList.getItems().get(0).getMetadata().getOwnerReferences().get(0).getName();

                if (hpaTargetKind.equals(podOwnerKind) && hpaTargetName.equals(podOwnerName)) {
                    isHPA = true;
                } else if (hpaTargetKind.equals("Deployment")) {
                    HashMap replicaSetHashMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                            propertyService.getCpMasterApiListReplicaSetsGetUrl(), HttpMethod.GET, null, Map.class, params);
                    ReplicaSetsList replicaSetsList = commonService.setResultObject(replicaSetHashMap, ReplicaSetsList.class);
                    String replicaSetOwnerName = replicaSetsList.getItems().get(0).getMetadata().getOwnerReferences().get(0).getName();

                    if (hpaTargetName.equals(replicaSetOwnerName)) {
                        isHPA = true;
                    }

                }
            }
        }

        HashMap responseMap = null;

        if (isHPA) {
            responseMap = (HashMap) restTemplateService.send(Constants.TARGET_COMMON_API,
                    "/chaos/resourceUsageByHpaPod/{chaosName}".replace("{chaosName}", params.getName()), HttpMethod.GET, null, Map.class, params);
        } else {
            responseMap = (HashMap) restTemplateService.send(Constants.TARGET_COMMON_API,
                    "/chaos/resourceUsageByPod/{chaosName}".replace("{chaosName}", params.getName()), HttpMethod.GET, null, Map.class, params);
        }

        ResourceUsage resourceUsage = commonService.setResultObject(responseMap, ResourceUsage.class);
        return (ResourceUsage) commonService.setResultModel(resourceUsage, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Resource usage by workload for selected Pods during chao 조회(Get Resource usage by workload for selected Pods during chao)
     *
     * @param params the params
     * @return the ResourceUsage
     */
    public ResourceUsage getResourceUsageByWorkload(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_COMMON_API,
                "/chaos/resourceUsageByWorkload/{chaosName}".replace("{chaosName}", params.getName()), HttpMethod.GET, null, Map.class, params);

        ResourceUsage resourceUsage = commonService.setResultObject(responseMap, ResourceUsage.class);
        return (ResourceUsage) commonService.setResultModel(resourceUsage, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Resource usage by node during chaos 조회(Get Resource usage by node during chaos)
     *
     * @param params the params
     * @return the ResourceUsage
     */
    public ResourceUsage getResourceUsageByNode(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_COMMON_API,
                "/chaos/resourceUsageByNode/{chaosName}".replace("{chaosName}", params.getName()), HttpMethod.GET, null, Map.class, params);

        ResourceUsage resourceUsage = commonService.setResultObject(responseMap, ResourceUsage.class);
        return (ResourceUsage) commonService.setResultModel(resourceUsage, Constants.RESULT_STATUS_SUCCESS);
    }


}
