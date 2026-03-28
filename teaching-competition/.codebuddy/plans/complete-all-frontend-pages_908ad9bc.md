---
name: complete-all-frontend-pages
overview: 完成教学竞赛系统前端所有剩余页面和功能：9个空白页面从零开发 + 4个模拟数据页面替换为真实API集成
todos:
  - id: api-integration-simulated
    content: 替换4个模拟数据页面的TODO为真实API调用（admin/Competitions + super-admin/Users + participant/Dashboard + super-admin/Dashboard）
    status: completed
  - id: guest-awards
    content: 创建 guest/Awards.vue 往届成果展示页
    status: completed
  - id: participant-pages
    content: 创建3个参赛者页面（Registrations + Works + Scores）
    status: completed
    dependencies:
      - api-integration-simulated
  - id: admin-pages
    content: 创建4个管理员页面（Dashboard + Registrations + Reviews + Results）
    status: completed
    dependencies:
      - api-integration-simulated
  - id: super-admin-logs
    content: 创建 super-admin/Logs.vue 操作日志页面
    status: completed
    dependencies:
      - api-integration-simulated
  - id: final-verification
    content: 最终验证：检查所有页面的路由、API调用、权限控制是否正确
    status: completed
    dependencies:
      - guest-awards
      - participant-pages
      - admin-pages
      - super-admin-logs
---

## 用户需求

完成教学竞赛管理系统前端所有剩余的页面和功能，共涉及 13 个页面：

### 9 个空白页面（从零开发）

1. **guest/Awards.vue** — 往届成果展示页，展示已完成竞赛的获奖信息
2. **participant/Registrations.vue** — 我的报名列表，查看/管理个人报名记录
3. **participant/Works.vue** — 作品管理，上传/查看作品，支持文件上传
4. **participant/Scores.vue** — 成绩查询，查看竞赛评审成绩
5. **admin/Dashboard.vue** — 竞赛管理员首页，统计数据概览
6. **admin/Registrations.vue** — 报名审核，通过/驳回报名申请
7. **admin/Reviews.vue** — 评审分配，分配评审任务给评委
8. **admin/Results.vue** — 成绩公示，查看/发布竞赛成绩
9. **super-admin/Logs.vue** — 操作日志，分页查询系统日志

### 4 个模拟数据页面（替换为真实 API）

1. **participant/Dashboard.vue** — 替换模拟数据为真实竞赛列表
2. **admin/Competitions.vue** — 替换模拟数据为真实 CRUD 操作
3. **super-admin/Users.vue** — 替换模拟数据为真实用户管理
4. **super-admin/Dashboard.vue** — 保留模拟数据（后端无 Dashboard API）

## 技术栈

- **前端框架**: Vue 3 Composition API (`<script setup>`)
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **HTTP 请求**: Axios 封装的 `request` 工具
- **日期处理**: dayjs
- **路由**: Vue Router 4

## 实现方案

### 核心原则

1. **复用现有架构**: 所有页面严格遵循项目已有的代码风格（import 顺序、命名约定、CSS 变量、响应式断点）
2. **API 集成优先**: 4 个模拟数据页面只替换数据源（模拟 setTimeout/硬编码 → 真实 API 调用），不改动 UI 结构和样式
3. **新建页面参照已有模板**: 空白页面参照 `super-admin/Users.vue`（表格+搜索+CRUD弹窗）和 `guest/Competitions.vue`（表格+分页+搜索筛选）的模式
4. **后端无 Dashboard API**: admin/Dashboard.vue 和 super-admin/Dashboard.vue 的统计数据使用已有接口聚合或保留模拟数据

### 关键技术决策

- **v-loading 统一放在 el-table 上**: 避免之前 v-loading 与其他指令冲突的问题
- **API 响应结构**: 列表接口统一 `res.data.records` + `res.data.total`，详情/单对象用 `res.data`
- **状态枚举**: 统一使用大写（DRAFT/PUBLISHED/REGISTRATION/ONGOING/FINISHED 等）
- **公共接口不传 token**: request.js 已配置 `/public/` 路径跳过 token

### 后端 API 对应关系（已验证）

| 页面 | 调用 API | 备注 |
| --- | --- | --- |
| guest/Awards.vue | getFinishedAwards, getCompetitionResults | 获取已结束竞赛的获奖信息 |
| participant/Registrations.vue | getRegistrations (GET /registration/my) | 分页、page/size 参数 |
| participant/Works.vue | getMyWorks, uploadWork | uploadWork 用 multipart/form-data |
| participant/Scores.vue | getCompetitionResults (GET /award/results/{id}) | 需要选择竞赛 |
| admin/Dashboard.vue | 无专属 API，保留模拟数据 | 后端无统计接口 |
| admin/Registrations.vue | getRegistrationList, approveRegistration, rejectRegistration | 需传 competitionId |
| admin/Reviews.vue | assignReview, submitScore, getReviewScores | 分配评审任务 |
| admin/Results.vue | getAwardList, publishAward, publishAnnouncement, getCompetitionResults | 发布奖项和公示 |
| super-admin/Logs.vue | getOperationLogs | module/username/startDate/endDate/分页 |


### 模拟数据替换策略

- **admin/Competitions.vue**: 将 `fetchCompetitions` 中的 `setTimeout + 模拟数组` 替换为 `await getAdminCompetitions()`；将 `handleSubmit` 中的 TODO 替换为 `createCompetition/updateCompetition`；将 `handleDelete` 替换为 `deleteCompetition`
- **super-admin/Users.vue**: 将 `fetchUsers` 替换为 `await getUserList()`；将 `handleFreeze/handleUnfreeze` 替换为 API 调用；将 `handleResetPassword` 替换为 `resetUserPassword`；将 `handleDelete` 替换为 `deleteUser`；将 `handleSubmit` 替换为 `createUser/updateUser`
- **participant/Dashboard.vue**: 将 `fetchOngoingCompetitions` 替换为 `await getPublicCompetitions({ status: 'REGISTRATION' })`；将 `handleRegister` 替换为调用 `createRegistration`
- **super-admin/Dashboard.vue**: 保留模拟数据，不做 API 替换

## 架构设计

```
前端页面分层:
├── guest/ (公开页面)
│   ├── Competitions.vue     [已完成] 公开竞赛列表
│   ├── CompetitionDetail.vue [已完成] 竞赛详情+报名
│   └── Awards.vue           [新建] 往届获奖成果
├── participant/ (参赛者页面)
│   ├── Dashboard.vue        [修改] 替换模拟数据
│   ├── Registrations.vue    [新建] 我的报名
│   ├── Works.vue            [新建] 作品管理
│   └── Scores.vue           [新建] 成绩查询
├── admin/ (管理员页面)
│   ├── Dashboard.vue        [新建] 管理首页(模拟数据)
│   ├── Competitions.vue     [修改] 替换模拟数据
│   ├── Registrations.vue    [新建] 报名审核
│   ├── Reviews.vue          [新建] 评审分配
│   └── Results.vue          [新建] 成绩公示
└── super-admin/ (超管页面)
    ├── Dashboard.vue        [修改] 保留模拟数据
    ├── Users.vue            [修改] 替换模拟数据
    └── Logs.vue             [新建] 操作日志
```

## 目录结构

```
frontend/src/views/
├── guest/
│   ├── Awards.vue                  # [新建] 往届成果页
│   ├── Competitions.vue            # [不修改] 已完成
│   └── CompetitionDetail.vue       # [不修改] 已完成
├── participant/
│   ├── Dashboard.vue               # [修改] 替换 fetchOngoingCompetitions 为真实API
│   ├── Registrations.vue           # [新建] 我的报名列表
│   ├── Works.vue                   # [新建] 作品管理(含上传)
│   └── Scores.vue                  # [新建] 成绩查询
├── admin/
│   ├── Dashboard.vue               # [新建] 管理员首页(模拟数据)
│   ├── Competitions.vue            # [修改] 替换所有TODO为真实API
│   ├── Registrations.vue           # [新建] 报名审核
│   ├── Reviews.vue                 # [新建] 评审分配
│   └── Results.vue                 # [新建] 成绩公示
└── super-admin/
    ├── Dashboard.vue               # [修改] 不改数据源，仅补充交互
    ├── Users.vue                   # [修改] 替换所有TODO为真实API
    └── Logs.vue                    # [新建] 操作日志查询
```

## 实现注意事项

- 所有 `v-loading` 只放在 `el-table` 或页面最外层 `div` 上，避免与其他指令冲突
- 所有 `import` 语句集中在 `<script setup>` 顶部
- API 函数的参数名必须与后端 `@RequestParam` 名称完全一致（如 `page/size/keyword/role/status/competitionId`）
- 表格操作列使用 `scope.row` 获取行数据，`link` 类型按钮
- 文件上传使用 `multipart/form-data`，后端 `WorkController` 接收 `@RequestParam("file") MultipartFile` + `WorkDTO`
- `el-empty` 组件用于列表无数据时的空状态提示

## Agent Extensions

### SubAgent

- **code-explorer**
- Purpose: 在创建新页面时，快速搜索已有页面中的 CSS 变量、代码模式和组件用法，确保新页面与现有代码风格一致
- Expected outcome: 准确提取已有页面的通用样式模式（如分页、表格卡片、搜索筛选区域的 CSS 类名），在新页面中复用