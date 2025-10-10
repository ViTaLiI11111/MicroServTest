# ---------- build stage ----------
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Копіюємо csproj та робимо restore окремо (кешування)
COPY ["MenuService.csproj", "./"]
RUN dotnet restore "./MenuService.csproj"

# Копіюємо решту коду і publish
COPY . .
RUN dotnet publish "MenuService.csproj" -c Release -o /app/publish /p:UseAppHost=false

# ---------- runtime stage ----------
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS runtime
WORKDIR /app
ENV ASPNETCORE_URLS=http://+:80

COPY --from=build /app/publish .
EXPOSE 80

ENTRYPOINT ["dotnet", "MenuService.dll"]
