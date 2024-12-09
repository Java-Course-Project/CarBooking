package org.duyvu.carbooking.configuration.filter;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.utils.distributed.DistributedRateLimiter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class RateLimiterFilter extends OncePerRequestFilter {
	private final DistributedRateLimiter rateLimiter;

	private static final Duration TIMEOUT = Duration.ofSeconds(5);

	@Override
	protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
									@Nonnull FilterChain filterChain) throws ServletException, IOException {
		if (!rateLimiter.tryAcquire(TIMEOUT)) {
			response.sendError(429, "Too many requests");
			return;
		}
		filterChain.doFilter(request, response);
	}
}
