# ===========================
# 1. Build stage
# ===========================
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Копіюємо всі csproj
COPY OrderDispatch.Api/*.csproj OrderDispatch.Api/
COPY OrderDispatch.Application/*.csproj OrderDispatch.Application/
COPY OrderDispatch.Domain/*.csproj OrderDispatch.Domain/
COPY OrderDispatch.Infrastructure/*.csproj OrderDispatch.Infrastructure/
COPY OrderDispatch.Workers/*.csproj OrderDispatch.Workers/

# Відновлення залежностей
RUN dotnet restore OrderDispatch.Api/OrderDispatch.Api.csproj

# Копіюємо решту коду
COPY . .

# Збірка
WORKDIR /src/OrderDispatch.Api
RUN dotnet publish -c Release -o /app/publish /p:UseAppHost=false


# ===========================
# 2. Runtime stage
# ===========================
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS runtime
WORKDIR /app
COPY --from=build /app/publish .

# --- ВАЖЛИВО: Копіюємо файл ключів Firebase ---
# Оскільки контекст білда - це API/OrderDispatchService,
# а файл лежить всередині підпапки OrderDispatch.Api
COPY OrderDispatch.Api/firebase-service-account.json .
# ---------------------------------------------

# Порт
EXPOSE 8080

# Змінна середовища
ENV ASPNETCORE_URLS=http://+:8080
ENTRYPOINT ["dotnet", "OrderDispatch.Api.dll"]