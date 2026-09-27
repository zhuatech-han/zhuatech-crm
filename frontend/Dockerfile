# Copyright 2026 Shanghai Rujing Zhihua Information Technology Co., Ltd. · 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2
FROM node:24-alpine AS build
WORKDIR /workspace
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build
FROM nginx:1.29-alpine
LABEL org.opencontainers.image.vendor="上海如静知华信息科技有限公司" org.opencontainers.image.source="https://www.zhuatech.cn/"
COPY --from=build /workspace/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
